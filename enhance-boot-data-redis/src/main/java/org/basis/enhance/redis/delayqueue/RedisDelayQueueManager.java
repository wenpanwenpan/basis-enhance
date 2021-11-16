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
 * redis延时队列管理器
 *
 * @author Mr_wenpan@163.com 2021/11/15 21:03
 */
public class RedisDelayQueueManager {

    private static final Map<String, RedisDelayQueue> REDIS_DELAY_QUEUE_MAP = new ConcurrentHashMap<>(16);

    private static final Map<String, ExecutorService> REDIS_DELAY_EXECUTOR_MAP = new ConcurrentHashMap<>(16);

    /**
     * 注册延时队列
     */
    public static void register(String delayQueueName, ExecutorService threadPoolExecutor) {
        REDIS_DELAY_EXECUTOR_MAP.putIfAbsent(delayQueueName, threadPoolExecutor);
    }

    /**
     * 批量注册延时队列
     */
    public static void batchregister(Map<String, ThreadPoolExecutor> redisDelayExecutorMap) {
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
    public static <T extends Serializable> Boolean add(String delayQueueName, T data) {
        RedisDelayQueue<T> redisDelayQueue = REDIS_DELAY_QUEUE_MAP.get(delayQueueName);
        if (Objects.isNull(redisDelayQueue)) {
            throw new RuntimeException(String.format("add to delay queue [%s] not exists ，please check delay queue name!", delayQueueName));
        }
        return redisDelayQueue.offer(data);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> Boolean add(String delayQueueName, T data, int delayed, TimeUnit timeUnit) {
        RedisDelayQueue<T> redisDelayQueue = REDIS_DELAY_QUEUE_MAP.get(delayQueueName);
        if (Objects.isNull(redisDelayQueue)) {
            throw new RuntimeException(String.format("add to delay queue [%s] not exists ，please check delay queue name!", delayQueueName));
        }
        return redisDelayQueue.offer(data, delayed, timeUnit);
    }

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
