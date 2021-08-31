package org.basis.enhance.multisource;

import org.basis.enhance.config.DynamicRedisTemplateFactory;
import org.basis.enhance.config.properties.RedisDataSourceProperties;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;

import java.util.ArrayList;

/**
 * redis 多数据源上下文
 *
 * @author Mr_wenpan@163.com 2021/8/31 11:23 上午
 */
public abstract class RedisDataSourceContext implements ApplicationContextAware {

    public static final String FIELD_DATASOURCE_NAME = "dataSourceName";

    protected ApplicationContext applicationContext;
    protected String dataSourceName;


    protected RedisProperties getRedisProperties() {
        RedisDataSourceProperties dataSourceProperties = applicationContext.getBean(RedisDataSourceProperties.class);
        return dataSourceProperties.getDatasource().get(dataSourceName);
    }

    protected JedisClientConfigurationBuilderCustomizer getJedisBuilderCustomizers() {
        applicationContext.getBeansOfType(JedisClientConfigurationBuilderCustomizer.class).values();
        return new ArrayList<>(applicationContext
                .getBeansOfType(JedisClientConfigurationBuilderCustomizer.class).values()).get(0);
    }

    protected LettuceClientConfigurationBuilderCustomizer getLettuceBuilderCustomizers() {
        return new ArrayList<>(applicationContext
                .getBeansOfType(LettuceClientConfigurationBuilderCustomizer.class).values()).get(0);
    }

    protected RedisSentinelConfiguration getSentinelConfiguration() {
        return new ArrayList<>(applicationContext.getBeansOfType(RedisSentinelConfiguration.class).values()).get(0);
    }

    protected RedisClusterConfiguration getRedisClusterConfiguration() {
        return new ArrayList<>(applicationContext.getBeansOfType(RedisClusterConfiguration.class).values()).get(0);
    }

    protected DynamicRedisTemplateFactory<String, String> getDynamicRedisTemplateFactory() {
        RedisProperties redisProperties = getRedisProperties();
        JedisClientConfigurationBuilderCustomizer jedisBuilderCustomizers = getJedisBuilderCustomizers();
        LettuceClientConfigurationBuilderCustomizer lettuceBuilderCustomizers = getLettuceBuilderCustomizers();
        RedisSentinelConfiguration sentinelConfiguration = getSentinelConfiguration();
        RedisClusterConfiguration redisClusterConfiguration = getRedisClusterConfiguration();

        return new DynamicRedisTemplateFactory<String, String>(redisProperties, sentinelConfiguration,
                redisClusterConfiguration, jedisBuilderCustomizers, lettuceBuilderCustomizers);
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}