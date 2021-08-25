package org.basis.enhance.mybatis.model;

import lombok.Data;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

import java.util.List;

/**
 * 绑定SQL帮助器
 *
 * @author Mr_wenpan@163.com 2021/8/11 4:18 下午
 */
@Data
public class BoundSqlHelper {

    /**
     * mybatis的boundSql
     */
    private BoundSql boundSql;

    /**
     * SQL语句
     */
    private String sql;

    /**
     * 主键名
     */
    private String primaryKey;

    /**
     * 主键类型处理器
     */
    private TypeHandler typeHandler;

    /**
     * mybatis全局配置信息
     */
    private Configuration configuration;

    /**
     * 是否早已经包含了主键
     */
    private boolean isAleadyIncludePrimaryKey;

    /**
     * 是否批量操作
     */
    private boolean isInsertBatch;

    /**
     * 主键集合
     */
    private List<String> primaryKeyList;
}
