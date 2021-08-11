package org.basis.enhance.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis connection configuration using Jedis.
 * 当使用jedis客户端时，jedis客户端的配置信息
 *
 * @author Mark Paluch
 * @author Stephane Nicoll
 */
class JedisConnectionConfigure extends RedisConnectionConfiguration {

    private final ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers;

    JedisConnectionConfigure(RedisProperties properties,
                             ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                             ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
                             ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers,
                             int database) {
        super(properties, sentinelConfiguration, clusterConfiguration, database);
        this.builderCustomizers = builderCustomizers;
    }

    /**
     * 创建jedis连接工厂
     */
    JedisConnectionFactory redisConnectionFactory() {
        return createJedisConnectionFactory();
    }

    /**
     * 创建jedis连接工厂
     */
    private JedisConnectionFactory createJedisConnectionFactory() {
        JedisClientConfiguration clientConfiguration = getJedisClientConfiguration(builderCustomizers);
        JedisConnectionFactory jedisConnectionFactory;
        if (getSentinelConfig() != null) {
            jedisConnectionFactory = new JedisConnectionFactory(getSentinelConfig(), clientConfiguration);
        } else if (getClusterConfiguration() != null) {
            jedisConnectionFactory = new JedisConnectionFactory(getClusterConfiguration(), clientConfiguration);
        } else {
            jedisConnectionFactory = new JedisConnectionFactory(getStandaloneConfig(), clientConfiguration);
        }
        // 由于我们手动创建jedisConnectionFactory连接工厂，所以afterPropertiesSet并不会像spring一样自动被吊起
        // 必须手动调用afterPropertiesSet()，初始化connectionProvider，不然创建连接会报错空指针
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    /**
     * 获取jedis客户端配置
     */
    private JedisClientConfiguration getJedisClientConfiguration(
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder());
        RedisProperties.Pool pool = getProperties().getJedis().getPool();
        if (pool != null) {
            applyPooling(pool, builder);
        }
        if (StringUtils.hasText(getProperties().getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    private JedisClientConfigurationBuilder applyProperties(JedisClientConfigurationBuilder builder) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(getProperties().isSsl()).whenTrue().toCall(builder::useSsl);
        map.from(getProperties().getTimeout()).to(builder::readTimeout);
        map.from(getProperties().getConnectTimeout()).to(builder::connectTimeout);
        map.from(getProperties().getClientName()).whenHasText().to(builder::clientName);
        return builder;
    }

    private static void applyPooling(RedisProperties.Pool pool,
                                     JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig(pool));
    }

    /**
     * jedis连接池配置
     */
    private static JedisPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        if (pool.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRunsMillis(pool.getTimeBetweenEvictionRuns().toMillis());
        }
        if (pool.getMaxWait() != null) {
            config.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        return config;
    }

    /**
     * 从redis url中自定义配置
     */
    private void customizeConfigurationFromUrl(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = RedisConnectionConfiguration.parseUrl(getProperties().getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

}
