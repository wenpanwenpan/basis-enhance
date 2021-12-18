package org.basis.enhance.mongo.config.properties;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * MongoDB多数据源properties
 *
 * @author Mr_wenpan@163.com 2021/12/16 21:19
 */
@ConfigurationProperties(prefix = MongoDataSourceProperties.PREFIX)
public class MongoDataSourceProperties {

    static final String PREFIX = "spring.data.mongodb";

    /**
     * 数据源配置集合（key: 数据源名称， value: 数据源对应的Mongo配置）
     */
    private Map<String, MongoProperties> datasource = new HashMap<>();

    public Map<String, MongoProperties> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, MongoProperties> datasource) {
        this.datasource = datasource;
    }

}
