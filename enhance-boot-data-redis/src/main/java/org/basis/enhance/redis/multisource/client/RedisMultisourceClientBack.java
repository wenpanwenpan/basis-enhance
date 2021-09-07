package org.basis.enhance.redis.multisource.client;

import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.redis.multisource.strategy.RedisShardingStrategy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis多数据源客户端
 *
 * @author Mr_wenpan@163.com 2021/9/4 6:35 下午
 */
public class RedisMultisourceClientBack {

    private final Map<String, RedisConnectionFactory> redisConnectionFactories;

    private final Map<String, StringRedisTemplate> redisTemplates;

    public RedisMultisourceClientBack() {
        redisConnectionFactories = new ConcurrentHashMap<>();
        redisTemplates = new ConcurrentHashMap<>();
    }

    public Map<String, RedisConnectionFactory> getRedisConnectionFactories() {
        return redisConnectionFactories;
    }

    public Map<String, StringRedisTemplate> getRedisTemplates() {
        return redisTemplates;
    }

    public void addRedisConnectionFactories(String instanceName, RedisConnectionFactory redisConnectionFactory) {
        redisConnectionFactories.put(StringUtils.lowerCase(instanceName), redisConnectionFactory);
    }

    public void addRedisTemplates(String instanceName, StringRedisTemplate redisTemplates) {
        this.redisTemplates.put(StringUtils.lowerCase(instanceName), redisTemplates);
    }

    public <T> StringRedisTemplate getRedisTemplate(RedisShardingStrategy<T> shardingStrategy, T strategyCondition) {
        String redisInstanceName = shardingStrategy.redisShardingInstance(strategyCondition);
        return redisTemplates.get(StringUtils.lowerCase(redisInstanceName));
    }

    public <T> RedisConnectionFactory getRedisConnectionFactory(RedisShardingStrategy<T> shardingStrategy, T strategyCondition) {
        String redisInstanceName = shardingStrategy.redisShardingInstance(strategyCondition);
        return redisConnectionFactories.get(StringUtils.lowerCase(redisInstanceName));
    }

    public StringRedisTemplate getRedisTemplate(String instanceName) {
        return redisTemplates.get(StringUtils.lowerCase(instanceName));
    }

    public RedisConnectionFactory getRedisConnectionFactory(String instanceName) {
        return redisConnectionFactories.get(StringUtils.lowerCase(instanceName));
    }
}