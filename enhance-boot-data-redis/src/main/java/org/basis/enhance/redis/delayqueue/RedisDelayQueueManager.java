package org.basis.enhance.redis.delayqueue;

import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * redis延时队列管理器，统一管理每个延时队列 + 延时队列所用的线程池。后期可做队列监控和线程池监控
 *
 * @author Mr_wenpan@163.com 2021/11/15 21:03
 */
@SuppressWarnings("rawtypes")
public class RedisDelayQueueManager {

    private static final Map<String, RedisDelayQueue> REDIS_DELAY_QUEUE_MAP = new ConcurrentHashMap<>(16);

    private static final Map<String, ExecutorService> REDIS_DELAY_EXECUTOR_MAP = new ConcurrentHashMap<>(16);

    /**
     * 注册延时队列对应的线程池
     */
    public static void register(String delayQueueName, ExecutorService threadPoolExecutor) {
        REDIS_DELAY_EXECUTOR_MAP.putIfAbsent(delayQueueName, threadPoolExecutor);
    }

    /**
     * 批量注册延时队列对应的线程池
     */
    public static void batchregist(Map<String, ThreadPoolExecutor> redisDelayExecutorMap) {
        if (MapUtils.isEmpty(redisDelayExecutorMap)) {
            return;
        }
        redisDelayExecutorMap.forEach(redisDelayExecutorMap::putIfAbsent);
    }

    public static ExecutorService getExecutorByQueueName(String delayQueueName) {
        return REDIS_DELAY_EXECUTOR_MAP.get(delayQueueName);
    }

    /**
     * 注册延时队列
     */
    public static <T extends Serializable> void register(RedisDelayQueue<T> redisDelayQueue) {
        REDIS_DELAY_QUEUE_MAP.putIfAbsent(redisDelayQueue.queue(), redisDelayQueue);
    }

    /**
     * 批量注册延时队列
     */
    public static void batchRegister(Map<String, RedisDelayQueue> redisDelayQueueMap) {
        if (MapUtils.isEmpty(redisDelayQueueMap)) {
            return;
        }
        redisDelayQueueMap.forEach((delayQueueName, redisDelayQueue) -> {
            REDIS_DELAY_QUEUE_MAP.putIfAbsent(redisDelayQueue.queue(), redisDelayQueue);
        });
    }

    public static RedisDelayQueue getDelayQueueByName(String delayQueueName) {
        return REDIS_DELAY_QUEUE_MAP.get(delayQueueName);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> Boolean offer(String delayQueueName, T data) {
        RedisDelayQueue<T> redisDelayQueue = REDIS_DELAY_QUEUE_MAP.get(delayQueueName);
        if (Objects.isNull(redisDelayQueue)) {
            throw new RuntimeException(String.format("add to delay queue [%s] not exists ，please check delay queue name!", delayQueueName));
        }
        return redisDelayQueue.offer(data);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> Boolean offer(String delayQueueName, T data, int delayed, TimeUnit timeUnit) {
        RedisDelayQueue<T> redisDelayQueue = REDIS_DELAY_QUEUE_MAP.get(delayQueueName);
        if (Objects.isNull(redisDelayQueue)) {
            throw new RuntimeException(String.format("add to delay queue [%s] not exists ，please check delay queue name!", delayQueueName));
        }
        return redisDelayQueue.offer(data, delayed, timeUnit);
    }

    /**
     * 从延时队列删除元素
     *
     * @param delayQueueName 延时队列名称
     * @param data           数据
     * @return java.lang.Boolean true / false
     * @author Mr_wenpan@163.com 2021/11/16 10:42 下午
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> Boolean remove(String delayQueueName, T data) {
        RedisDelayQueue<T> redisDelayQueue = REDIS_DELAY_QUEUE_MAP.get(delayQueueName);
        if (Objects.isNull(redisDelayQueue)) {
            throw new RuntimeException(String.format("remove from delay queue [%s] not exists ，please check delay queue name!", delayQueueName));
        }
        return redisDelayQueue.remove(data);
    }

    public static void clear() {
        REDIS_DELAY_QUEUE_MAP.clear();
    }

}
