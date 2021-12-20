package org.basis.enhance.mongo.config.properties;

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

    public static final String PREFIX = "spring.data.mongodb";

    /**
     * 数据源配置集合（key: 数据源名称， value: 数据源对应的Mongo配置）
     */
    private Map<String, EnhanceMongoProperties> datasource;

    /**
     * 是否开启多数据源分片（默认不开启）
     */
    private Boolean enableSharding;

    public MongoDataSourceProperties() {
        enableSharding = Boolean.FALSE;
        datasource = new HashMap<>();
    }

    public Boolean getEnableSharding() {
        return enableSharding;
    }

    public void setEnableSharding(Boolean enableSharding) {
        this.enableSharding = enableSharding;
    }

    public Map<String, EnhanceMongoProperties> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, EnhanceMongoProperties> datasource) {
        this.datasource = datasource;
    }

}
