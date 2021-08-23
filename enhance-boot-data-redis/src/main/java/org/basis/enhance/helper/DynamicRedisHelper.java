package org.basis.enhance.helper;

import org.basis.enhance.template.DynamicRedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;

/**
 * Redis操作工具类，集成封装一些常用方法，支持动态切换DB
 *
 * @author Mr_wenpan@163.com 2021/8/7 2:54 下午
 */
public class DynamicRedisHelper extends RedisHelper {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRedisHelper.class);

    /**
     * 动态redisTemplate
     */
    private final DynamicRedisTemplate<String, String> redisTemplate;

    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24L;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;

    public DynamicRedisHelper(DynamicRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取RedisTemplate对象
     * <p>
     * redisTemplate
     */
    @Override
    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 更改当前线程 RedisTemplate database
     *
     * @param database set current redis database
     */
    @Override
    public void setCurrentDatabase(int database) {
        RedisDatabaseThreadLocalHelper.set(database);
    }

    @Override
    public void clearCurrentDatabase() {
        RedisDatabaseThreadLocalHelper.clear();
    }

    @Override
    protected ValueOperations<String, String> getValueOperations() {
        return getRedisTemplate().opsForValue();
    }

    @Override
    protected HashOperations<String, String, String> getHashOperations() {
        return getRedisTemplate().opsForHash();
    }

    @Override
    protected ListOperations<String, String> getListOperations() {
        return getRedisTemplate().opsForList();
    }

    @Override
    protected SetOperations<String, String> getSetOperations() {
        return getRedisTemplate().opsForSet();
    }

    @Override
    protected ZSetOperations<String, String> getZSetOperations() {
        return getRedisTemplate().opsForZSet();
    }
}
