package org.basis.enhance.template;

import org.basis.enhance.config.DynamicRedisTemplateFactory;
import org.basis.enhance.helper.RedisDatabaseThreadLocalHelper;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 动态 RedisTemplate ，以支持动态切换 redis database
 *
 * @author Mr_wenpan@163.com 2021/8/7 1:43 下午
 */
public class DynamicRedisTemplate<K, V> extends AbstractRoutingRedisTemplate<K, V> {

    /**
     * 动态RedisTemplate工厂，用于创建管理动态DynamicRedisTemplate
     */
    private final DynamicRedisTemplateFactory<K, V> dynamicRedisTemplateFactory;

    public DynamicRedisTemplate(DynamicRedisTemplateFactory<K, V> dynamicRedisTemplateFactory) {
        this.dynamicRedisTemplateFactory = dynamicRedisTemplateFactory;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return RedisDatabaseThreadLocalHelper.get();
    }

    /**
     * 通过制定的db创建RedisTemplate
     *
     * @param lookupKey db号
     * @return org.springframework.data.redis.core.RedisTemplate<K, V>
     * @author Mr_wenpan@163.com 2021/8/9 10:06 上午
     */
    @Override
    protected RedisTemplate<K, V> createRedisTemplateOnMissing(Object lookupKey) {
        return dynamicRedisTemplateFactory.createRedisTemplate((Integer) lookupKey);
    }

}