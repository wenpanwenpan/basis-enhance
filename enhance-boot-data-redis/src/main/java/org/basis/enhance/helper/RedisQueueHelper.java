package org.basis.enhance.helper;


import org.basis.enhance.config.properties.StoneRedisProperties;

/**
 * redis队列操作帮助器，基于Redis的消息队列，生产者消费者模式
 *
 * @author Mr_wenpan@163.com 2021/08/10 22:45
 */
public class RedisQueueHelper {

    private static final String PREFIX = "hzero-queue:";

    private final RedisHelper redisHelper;

    private final StoneRedisProperties properties;

    public RedisQueueHelper(RedisHelper redisHelper, StoneRedisProperties redisProperties) {
        this.redisHelper = redisHelper;
        properties = redisProperties;
    }

}
