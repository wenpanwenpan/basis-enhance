package org.basis.enhance.mybatis.interceptor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.type.LongTypeHandler;
import org.basis.enhance.mybatis.annotation.AutoId;
import org.basis.enhance.mybatis.model.BoundSqlHelper;
import org.basis.enhance.mybatis.process.BaseAutoIdProcess;
import org.basis.enhance.mybatis.process.SnowFlakeAutoIdProcess;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mybatis执行时拦截器
 *
 * @author Mr_wenpan@163.com 2021/8/13 10:55 下午
 */
@Intercepts(value = {@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class AutoIdInterceptor implements Interceptor {

    /**
     * key为类名，value：为id执行器
     */
    private Map<String, List<BaseAutoIdProcess>> idProcessMap = new ConcurrentHashMap<>();

    /**
     * key为类名，value为主键名
     */
    private Map<String, String> primaryKeyMap = new ConcurrentHashMap<>();

    /**
     * 批量插入分割符
     */
    private static final String INSERT_BATCH_KEY_SPILTE = ".";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        //args数组对应对象就是上面@Signature注解中args对应的对应类型
        MappedStatement mappedStatement = (MappedStatement) args[0];
        //实体对象
        Object entity = args[1];

        if ("insert".equalsIgnoreCase(mappedStatement.getSqlCommandType().name())) {
            BoundSqlHelper boundSqlHelper = prepareAutoIdAndNewSql(mappedStatement, entity);
            if (!boundSqlHelper.isAleadyIncludePrimaryKey()) {
                // 如果原SQL不包含主键插入，重置SQL
                resetSql2Invocation(invocation, boundSqlHelper, entity);
            }

        }

        return invocation.proceed();
    }

    /**
     * 设值自增ID并返回主键名
     *
     * @param entity 待插入的实体
     */
    private String setAutoIdAndReturnPrimaryKey(Object entity) throws Exception {
        List<BaseAutoIdProcess> baseAutoIdProcessList = getIdProcessList(entity);
        for (BaseAutoIdProcess baseAutoIdProcess : baseAutoIdProcessList) {
            baseAutoIdProcess.process(entity);
        }
        return baseAutoIdProcessList.get(0).getPrimaryKey();
    }

    /**
     * 设值主键值以及改变插入语句
     */
    private BoundSqlHelper prepareAutoIdAndNewSql(MappedStatement mappedStatement, Object entity) throws Exception {
        BoundSql boundSql = mappedStatement.getBoundSql(entity);
        String originSql = boundSql.getSql();
        // 通过SQL解析出Statement
        MySqlStatementParser mySqlStatementParser = new MySqlStatementParser(originSql);
        SQLStatement statement = mySqlStatementParser.parseStatement();
        // 转换为插入statement并且获取表名以及要插入的列
        SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) statement;
        SQLExprTableSource sqlTableSource = sqlInsertStatement.getTableSource();
        List<SQLExpr> columns = sqlInsertStatement.getColumns();
        String tableName = sqlTableSource.toString();

        // newSqlColumnsSb 用于拼接新的SQL语句
        StringBuilder newSqlColumnsSb = new StringBuilder();
        // 获取要插入的实体集合（单个插入或批量插入）
        Set<Object> entitySet = getEntitySet(entity);
        Object firstEntity = entitySet.iterator().next();
        String primaryKey = getPrimaryKey(firstEntity);

        int columnsIncludePrimaryKeyCount = 0;
        boolean isAleadyIncludePrimaryKey = false;
        for (SQLExpr column : columns) {
            String sqlColumn = column.toString().replace("`", "").replace("`", "");
            sqlColumn = StringUtils.deleteWhitespace(sqlColumn);
            // 如果主键名和要插入的SQL列名相同，则说明需要插入ID列
            if (primaryKey.equals(sqlColumn)) {
                columnsIncludePrimaryKeyCount++;
                isAleadyIncludePrimaryKey = true;
                continue;
            }
            newSqlColumnsSb.append(column.toString()).append(",");
        }

        newSqlColumnsSb.append(primaryKey);

        // 如果主键已经存在，则无需向下执行构造新sql，沿用旧sql，并填充id值即可
        if (isAleadyIncludePrimaryKey) {
            BoundSqlHelper boundSqlHelper = new BoundSqlHelper();
            boundSqlHelper.setAleadyIncludePrimaryKey(true);
            for (Object o : entitySet) {
                // 设置主键值，这步执行完后主键值就已经设置到entity实体中了
                setAutoIdAndReturnPrimaryKey(o);
            }
            return boundSqlHelper;
        }

        //构造出 insert into table （column1，column2，...）values
        StringBuilder newSqlPrefixSb = new StringBuilder();
        newSqlPrefixSb.append("insert into ").append(tableName).append("(").append(newSqlColumnsSb).append(") ").append("values ");

        //构造占位符，形如(?,?,?)
        String newSqlValuePlaceholder = StringUtils.repeat("?,", columns.size() - columnsIncludePrimaryKeyCount);
        newSqlValuePlaceholder = newSqlValuePlaceholder + "?";
        StringBuilder newSqlValuePlaceholderSb = new StringBuilder();
        newSqlValuePlaceholderSb.append("(").append(newSqlValuePlaceholder).append(")");
        StringBuilder newSqlSuffixSb = new StringBuilder();
        boolean isInsertBatch = isBatchInsert(boundSql);
        int idIndex = 0;
        List<String> primaryKeyList = new ArrayList<>();
        String insertBatchKeyPrefix = getInsertBatchKeyPrefix(boundSql);
        for (Object o : entitySet) {
            if (isInsertBatch) {
                String idProp = insertBatchKeyPrefix + idIndex + INSERT_BATCH_KEY_SPILTE + primaryKey;
                idIndex++;
                primaryKeyList.add(idProp);
            }
            // 给实体设置主键值
            setAutoIdAndReturnPrimaryKey(o);
            newSqlSuffixSb.append(newSqlValuePlaceholderSb).append(",");
        }
        // 删除最后一个,  形如：(?,?,?),(?,?,?),(?,?,?)
        newSqlSuffixSb.delete(newSqlSuffixSb.length() - 1, newSqlSuffixSb.length());
        // 构建成一条完整的新SQL
        String newSql = newSqlPrefixSb.append(newSqlSuffixSb).toString();

        // sql辅助类设值
        BoundSqlHelper boundSqlHelper = new BoundSqlHelper();
        boundSqlHelper.setPrimaryKey(primaryKey);
        boundSqlHelper.setTypeHandler(new LongTypeHandler());
        boundSqlHelper.setBoundSql(boundSql);
        boundSqlHelper.setSql(newSql);
        boundSqlHelper.setConfiguration(mappedStatement.getConfiguration());
        boundSqlHelper.setInsertBatch(isInsertBatch);
        boundSqlHelper.setPrimaryKeyList(primaryKeyList);
        System.out.println("===========>>>>>>>newSql " + newSql);
        return boundSqlHelper;
    }

    /**
     * 获取主键名
     *
     * @param entity 待插入的实体
     * @return 主键名
     */
    private String getPrimaryKey(Object entity) {
        String primaryKey = primaryKeyMap.get(entity.getClass().getName());
        if (StringUtils.isEmpty(primaryKey)) {
            // todo 同步器保证线程安全，后期这里可优化为更加细粒度的锁提高并发能力（参考：SimpleCache.keyLockMap）
            synchronized (this) {
                for (Field field : entity.getClass().getDeclaredFields()) {
                    AutoId autoId = field.getAnnotation(AutoId.class);
                    if (!ObjectUtils.isEmpty(autoId)) {
                        primaryKey = autoId.primaryKey();
                        primaryKeyMap.put(entity.getClass().getName(), primaryKey);
                        break;
                    }
                }
            }
        }
        return primaryKey;
    }

    /**
     * 判断是否是批量插入，如果是批量插入，其ParameterMapping的key形如：__frch_item_0.userName
     *
     * @param boundSql boundSql
     * @return 是否批量插入
     */
    private static boolean isBatchInsert(BoundSql boundSql) {
        // 获取参数映射
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (!CollectionUtils.isEmpty(parameterMappings)) {
            ParameterMapping parameterMapping = parameterMappings.get(0);
            String prop = parameterMapping.getProperty();
            return prop.contains(INSERT_BATCH_KEY_SPILTE);
        }
        return false;
    }

    /**
     * 获取批量插入key前缀，比如批量插入的key为__frch_user_0.userName
     *
     * @return 返回__frch_user_
     */
    private static String getInsertBatchKeyPrefix(BoundSql boundSql) {
        boolean isInsertBatch = isBatchInsert(boundSql);
        if (!isInsertBatch) {
            return null;
        }
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (!CollectionUtils.isEmpty(parameterMappings)) {
            ParameterMapping parameterMapping = parameterMappings.get(0);
            String prop = parameterMapping.getProperty();
            String prefix = StringUtils.substring(prop, 0, StringUtils.lastIndexOf(prop, INSERT_BATCH_KEY_SPILTE));
            return StringUtils.substring(prefix, 0, prefix.length() - 1);
        }

        return null;
    }


    /**
     * 获取id执行器列表
     * todo 为什么这里对一个ID的处理要设计为list？因为对于一个ID字段的生成要具备用户可定制，比如基因注入
     *
     * @param entity 待插入的实体
     * @return id执行器列表
     */
    private List<BaseAutoIdProcess> getIdProcessList(Object entity) {
        // 获取实体的类名
        String className = entity.getClass().getName();
        // 通过类名从缓存中获取到ID执行器
        List<BaseAutoIdProcess> idProcesses = idProcessMap.get(className);

        if (ObjectUtils.isEmpty(idProcesses)) {
            synchronized (this) {
                List<BaseAutoIdProcess> finalIdProcesses = new ArrayList<>();
                // 遍历entity中所有属性，找到标记有自动生成ID的注解的属性
                // todo 是否可以优化不用遍历
                ReflectionUtils.doWithFields(entity.getClass(), field -> {
                    ReflectionUtils.makeAccessible(field);
                    AutoId autoId = field.getAnnotation(AutoId.class);
                    // 必须持有自动生成id的注解并且字段类型为Long
                    if (!ObjectUtils.isEmpty(autoId) && (field.getType().isAssignableFrom(Long.class))) {
                        switch (autoId.type()) {
                            // 雪花算法生成ID
                            case SNOWFLAKE:
                                SnowFlakeAutoIdProcess snowFlakeAutoIdProcess = new SnowFlakeAutoIdProcess(field);
                                snowFlakeAutoIdProcess.setPrimaryKey(autoId.primaryKey());
                                finalIdProcesses.add(snowFlakeAutoIdProcess);
                                break;
                            case OTHER:
                                break;
                            default:
                                break;
                        }
                    }
                });
                // 添加到集合中缓存起来
                idProcessMap.put(className, finalIdProcesses);
            }
        }

        return idProcessMap.get(className);
    }


    /**
     * entity是需要插入的实体数据,它可能是对象,也可能是批量插入的对象。
     * 如果是单个对象,那么entity就是当前对象
     * 如果是批量插入对象，那么entity就是一个map集合,key值为"list",value为ArrayList集合对象
     */
    private static Set<Object> getEntitySet(Object entity) {
        Set<Object> set = new HashSet<>();
        // 解析批量插入的实体
        if (entity instanceof Map) {
            //批量插入对象
            Collection values = (Collection) ((Map) entity).get("list");
            System.out.println("values = " + values);
            for (Object value : values) {
                if (value instanceof Collection) {
                    set.addAll((Collection) value);
                } else {
                    set.add(value);
                }
            }
        } else {
            //单个插入对象
            set.add(entity);
        }
        return set;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 重置sql（即重置MappedStatement）
     *
     * @param invocation     invocation
     * @param boundSqlHelper boundSqlHelper
     * @param entity         待插入的实体
     */
    private void resetSql2Invocation(Invocation invocation, BoundSqlHelper boundSqlHelper, Object entity) throws SQLException {
        Object[] args = invocation.getArgs();
        // 获取原有的 MappedStatement
        MappedStatement statement = (MappedStatement) args[0];
        // 生成新的 MappedStatement
        MappedStatement newStatement = newMappedStatement(statement, new BoundSqlSqlSource(boundSqlHelper));
        MetaObject msObject = MetaObject.forObject(newStatement, new DefaultObjectFactory(),
                new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        msObject.setValue("sqlSource.boundSqlHelper.boundSql.sql", boundSqlHelper.getSql());
        /*for (ParameterMapping mapping : boundSqlHelper.getBoundSql().getParameterMappings()) {
            String prop = mapping.getProperty();
            System.out.println(prop);
            if (boundSqlHelper.getBoundSql().hasAdditionalParameter(prop)) {
                boundSqlHelper.getBoundSql().setAdditionalParameter(prop, boundSqlHelper.getBoundSql().getAdditionalParameter(prop));
            }
        }*/
        args[0] = newStatement;

    }

    private static MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(
                ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 参考源码
     */
    static class BoundSqlSqlSource implements SqlSource {
        private BoundSqlHelper boundSqlHelper;

        public BoundSqlSqlSource(BoundSqlHelper boundSqlHelper) {
            this.boundSqlHelper = boundSqlHelper;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            boolean isAleadyIncludePrimaryKey = boundSqlHelper.isAleadyIncludePrimaryKey();
            if (!isAleadyIncludePrimaryKey) {
                if (boundSqlHelper.isInsertBatch()) {
                    setPrimaryParameteMappingsAndSort();
                } else {
                    // 设置主键key
                    setPrimaryParameterMapping(boundSqlHelper.getPrimaryKey());
                }
            }

            return boundSqlHelper.getBoundSql();
        }

        /**
         * 设置批量插入的主键key，并对parameterMappings进行按key的索引下标排序。排序主要是让id值存放到他本该的存放的位置。
         * 如果不进行排序，则id值是追加到最后
         */
        private void setPrimaryParameteMappingsAndSort() {
            // 获取到主键列表
            List<String> primaryKeyList = boundSqlHelper.getPrimaryKeyList();
            for (String primaryKey : primaryKeyList) {
                setPrimaryParameterMapping(primaryKey);
            }

            List<ParameterMapping> parameterMappings = boundSqlHelper.getBoundSql().getParameterMappings();
            // 排序
            parameterMappings.sort((o1, o2) -> {
                int index1 = getInsertBatchKeyIndex(o1.getProperty());
                int index2 = getInsertBatchKeyIndex(o2.getProperty());
                return index1 - index2;
            });
        }

        private void setPrimaryParameterMapping(String primaryKey) {
            // 构建参数映射
            ParameterMapping parameterMapping = new ParameterMapping.Builder(boundSqlHelper.getConfiguration(),
                    primaryKey, boundSqlHelper.getTypeHandler()).build();
            // 获取到BoundSql并添加上主键映射
            boundSqlHelper.getBoundSql().getParameterMappings().add(parameterMapping);
        }


        /**
         * 获取批量插入前缀索引，形如：__frch_user_0.userName其索引就为0
         *
         * @param prop prop
         * @return 索引
         */
        private int getInsertBatchKeyIndex(String prop) {
            String prefix = StringUtils.substring(prop, 0, StringUtils.lastIndexOf(prop, INSERT_BATCH_KEY_SPILTE));
            String insertBatchKeyIndex = StringUtils.substring(prefix, prefix.length() - 1, prefix.length());
            return Integer.parseInt(insertBatchKeyIndex);
        }

    }

}
