package org.basis.enhance.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RedisProperties
 *
 * @author Mr_wenpan@163.com 2021/8/7 3:13 下午
 */
@ConfigurationProperties(prefix = StoneRedisProperties.PREFIX)
public class StoneRedisProperties {

    public static final String PREFIX = "stone.redis";

    /**
     * 是否开启动态数据库切换 默认开启，如果关闭需要在yml中配置stone.redis.dynamic-database=false
     */
    private boolean dynamicDatabase = true;

    /**
     * 启用redis消息队列
     */
    private boolean redisQueue = false;

    /**
     * 队列消息默认db
     */
    private int queueDb = 1;

    /**
     * 消费间隔时间，单位秒
     */
    private int intervals = 5;

    public boolean isDynamicDatabase() {
        return dynamicDatabase;
    }

    public void setDynamicDatabase(boolean dynamicDatabase) {
        this.dynamicDatabase = dynamicDatabase;
    }

    public int getQueueDb() {
        return queueDb;
    }

    public void setQueueDb(int queueDb) {
        this.queueDb = queueDb;
    }

    public boolean isRedisQueue() {
        return redisQueue;
    }

    public StoneRedisProperties setRedisQueue(boolean redisQueue) {
        this.redisQueue = redisQueue;
        return this;
    }

    public int getIntervals() {
        return intervals;
    }

    public StoneRedisProperties setIntervals(int intervals) {
        this.intervals = intervals;
        return this;
    }
}
