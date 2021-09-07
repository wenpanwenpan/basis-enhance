package org.basis.enhance.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.basis.enhance.redis.config.properties.RedisMultiSourceProperties;
import org.basis.enhance.redis.multisource.client.RedisMultisourceClientBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * redis多数据源自动配置(废弃)
 *
 * @author Mr_wenpan@163.com 2021/09/04 18:42
 */
@Deprecated
@Configuration
@EnableConfigurationProperties({RedisMultiSourceProperties.class})
public class EnhanceMultiSourceAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnhanceMultiSourceAutoConfiguration.class);

    private static final String REDIS_CLIENT_LETTUCE = "lettuce";

    private static final String REDIS_CLIENT_JEDIS = "jedis";

    @Bean
    @ConditionalOnProperty(prefix = "stone.redis.multisource.client", name = {"enabled"}, havingValue = "true")
    public RedisMultisourceClientBack redisShardingClientsFactory(RedisMultiSourceProperties redisMultiSourceProperties) {

        RedisMultisourceClientBack redisMultisourceClient = new RedisMultisourceClientBack();
        if (redisMultiSourceProperties.getInstances() == null || redisMultiSourceProperties.getInstances().size() < 1) {
            return redisMultisourceClient;
        }

        for (Map.Entry<String, RedisMultiSourceProperties.RedisConnector> properties : redisMultiSourceProperties.getInstances().entrySet()) {
            switch (getRedisClientType()) {
                case REDIS_CLIENT_LETTUCE:
                    buildLettuceRedisTemplate(redisMultiSourceProperties, properties, redisMultisourceClient);
                    break;
                case REDIS_CLIENT_JEDIS:
                    // 后面增加lettuce客户端的适配
                    throw new RuntimeException("upsupport jedis client，please user lettuce client!");
                default:
                    LOGGER.error("unknow redis client type.");
            }
        }

        return redisMultisourceClient;
    }

    /**
     * 构建客户端类型为lettuce的RedisTemplate
     */
    private void buildLettuceRedisTemplate(RedisMultiSourceProperties redisMultiSourceProperties, Map.Entry<String,
            RedisMultiSourceProperties.RedisConnector> properties, RedisMultisourceClientBack redisMultisourceClient) {

        RedisMultiSourceProperties.RedisConnector instanceProperties = properties.getValue();

        LettuceClientConfiguration lettuceClientConfiguration = buildLettuceClientConfig(
                redisMultiSourceProperties.getPoolMinIdle(),
                redisMultiSourceProperties.getPoolMaxIdle(),
                redisMultiSourceProperties.getPoolMaxWaitMillis(),
                instanceProperties.getTimeoutMillis());
        LettuceConnectionFactory lettuceConnectionFactory;

        if (StringUtils.isEmpty(instanceProperties.getMaster()) || StringUtils.isEmpty(instanceProperties.getNodes())) {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(instanceProperties.getHost());
            redisStandaloneConfiguration.setPort(instanceProperties.getPort());
            redisStandaloneConfiguration.setPassword(RedisPassword.of(instanceProperties.getPassword()));
            redisStandaloneConfiguration.setDatabase(instanceProperties.getDbIndex());
            lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
        } else {
            Set<String> sentinels = StringUtils.commaDelimitedListToSet(instanceProperties.getNodes());
            RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration(instanceProperties.getMaster(), sentinels);
            lettuceConnectionFactory = new LettuceConnectionFactory(sentinelConfiguration, lettuceClientConfiguration);
        }
        lettuceConnectionFactory.afterPropertiesSet();

        redisMultisourceClient.addRedisConnectionFactories(properties.getKey(), lettuceConnectionFactory);
        StringRedisTemplate redisTemplate = new StringRedisTemplate(lettuceConnectionFactory);
        redisMultisourceClient.addRedisTemplates(properties.getKey(), redisTemplate);
    }

    /**
     * 构建lettuce连接配置
     */
    private LettuceClientConfiguration buildLettuceClientConfig(int poolMinIdle, int poolMaxIdle,
                                                                long poolMaxWaitMillis, long commandTimeoutMillis) {
        GenericObjectPoolConfig redisPoolConfig = new GenericObjectPoolConfig();
        redisPoolConfig.setMinIdle(poolMinIdle);
        redisPoolConfig.setMaxIdle(poolMaxIdle);
        redisPoolConfig.setMaxTotal(poolMaxIdle);
        redisPoolConfig.setMaxWaitMillis(poolMaxWaitMillis);
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(redisPoolConfig)
                .commandTimeout(Duration.ofMillis(commandTimeoutMillis))
                .build();
    }

    /**
     * 构建jedis连接配置
     */
    private JedisClientConfiguration buildJedisClientConfig(int poolMinIdle, int poolMaxIdle, long poolMaxWaitMillis) {
        GenericObjectPoolConfig redisPoolConfig = new GenericObjectPoolConfig();
        redisPoolConfig.setMinIdle(poolMinIdle);
        redisPoolConfig.setMaxIdle(poolMaxIdle);
        redisPoolConfig.setMaxTotal(poolMaxIdle);
        redisPoolConfig.setMaxWaitMillis(poolMaxWaitMillis);
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jedisPoolingClientConfigurationBuilder =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        return jedisPoolingClientConfigurationBuilder
                .poolConfig(redisPoolConfig)
                .build();
    }

    /**
     * 获取Redis客户端的类型，提供jedis和lettuce两种
     */
    private static String getRedisClientType() {
        try {
            // 如果能加载lettuce，则优先使用lettuce
            Class.forName("io.lettuce.core.RedisClient");
            return REDIS_CLIENT_LETTUCE;
        } catch (ClassNotFoundException e) {
            LOGGER.debug("Not Lettuce redis client");
        }

        try {
            // 如果能加载Jedis，则使用Jedis
            Class.forName("redis.clients.jedis.Jedis");
            return REDIS_CLIENT_JEDIS;
        } catch (ClassNotFoundException e) {
            LOGGER.debug("Not Jedis redis client");
        }

        throw new RuntimeException("redis client not found.");
    }
}
