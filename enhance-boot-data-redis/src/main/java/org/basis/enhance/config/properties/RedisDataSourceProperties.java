package org.basis.enhance.config.properties;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * redis多数据源配置
 *
 * @author Mr_wenpan@163.com 2021/8/31 9:10 上午
 */
@ConfigurationProperties(prefix = RedisDataSourceProperties.PREFIX)
public class RedisDataSourceProperties {

    static final String PREFIX = "spring.redis";

    /**
     * 数据源配置集合
     */
    private Map<String, RedisProperties> datasource = new HashMap<>();

    public Map<String, RedisProperties> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, RedisProperties> datasource) {
        this.datasource = datasource;
    }
}