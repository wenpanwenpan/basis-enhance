package org.basis.enhance.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RedisProperties
 *
 * @author Mr_wenpan@163.com 2021/8/7 3:13 下午
 */
@Data
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
     * 是否启用Redis发布订阅
     */
    private boolean redisPubSub = false;

    /**
     * 队列消息默认db（即监控的队列的默认db）
     */
    private int queueDb = 1;

    /**
     * 消费间隔时间，单位秒
     */
    private int intervals = 5;

    /**
     * 线程池配置
     */
    private ThreadPoolProperties threadPoolProperties = new ThreadPoolProperties();

    @Data
    public static class ThreadPoolProperties {
        /**
         * 核心线程数 默认 2
         */
        private int corePoolSize = 2;
        /**
         * 最大线程数 默认 10
         */
        private int maxPoolSize = 10;
        /**
         * 线程完成任务后的待机存活时间 默认 60
         */
        private int keepAliveSeconds = 60;
        /**
         * 等待队列长度 默认 Integer.MAX_VALUE
         */
        private int queueCapacity = Integer.MAX_VALUE;
        /**
         * 是否允许停止闲置核心线程 默认 false
         */
        private boolean allowCoreThreadTimeOut = false;
        /**
         * 线程名前缀
         */
        private String threadNamePrefix = PREFIX;

    }

    public boolean isDynamicDatabase() {
        return dynamicDatabase;
    }

    public boolean getDynamicDatabase() {
        return dynamicDatabase;
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
