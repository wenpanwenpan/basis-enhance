package org.basis.enhance.mybatis.config;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

/**
 * pageHelper配置类
 * {参考 <a>https://pagehelper.github.io/docs/howtouse/#2-%E9%85%8D%E7%BD%AE%E6%8B%A6%E6%88%AA%E5%99%A8%E6%8F%92%E4%BB%B6</a>}
 *
 * @author Mr_wenpan@163.com 2022/05/03 19:24
 */
@Configuration
@ConditionalOnClass(SqlSessionFactory.class)
public class PageHelperConfig {

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @PostConstruct
    public void init() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        // 设置拦截器所需要的相关属性
        Properties properties = new Properties();
        // 数据库方言，MySQL
        properties.put("helperDialect", "mysql");
        properties.put("offsetAsPageNum", "false");
        properties.put("rowBoundsWithCount", "false");
        // 默认值为 false, 当该参数设置为 true 时，如果 pageSize=0 或者 RowBounds.limit = 0
        // 就会查询出全部的结果（相当于没有执行分页查询，但是返回结果仍然是 Page 类型）
        properties.put("pageSizeZero", "false");
        // 查询合理化，比如查询页数为-1，每页-100条这种默认会查询第一页
        properties.put("reasonable", "false");
        // 为了支持startPage(Object params)方法，增加了该参数来配置参数映射，用于从对象中根据属性名取值
        properties.put("params", "pageNum=pageNum;pageSize=pageSize;count=countSql;reasonable=true;pageSizeZero=pageSizeZero");
        properties.put("supportMethodsArguments", "false");
        // 在运行时根据多数据源自动识别对应方言的分页（一般配合多数据源使用）
        properties.put("autoRuntimeDialect", "false");
        // 通过这个参数来控制获取连接后，是否关闭该连接
        properties.put("closeConn", "true");
        pageInterceptor.setProperties(properties);

        // 添加分页拦截器
        sqlSessionFactoryList.forEach(sqlSessionFactory -> sqlSessionFactory.getConfiguration().addInterceptor(pageInterceptor));
    }

}
