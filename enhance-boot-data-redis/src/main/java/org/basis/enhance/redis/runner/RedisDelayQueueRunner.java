package org.basis.enhance.redis.runner;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections4.MapUtils;
import org.basis.enhance.redis.delayqueue.RedisDelayQueue;
import org.basis.enhance.redis.delayqueue.RedisDelayQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * redis延迟队列runner
 *
 * @author Mr_wenpan@163.com 2021/11/15 21:01
 */
public class RedisDelayQueueRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RedisDelayQueueRunner.class);

    @Autowired
    private ApplicationContext applicationContext;

    private ThreadPoolExecutor executor;

    @Override
    public void run(String... args) throws Exception {
        Map<String, RedisDelayQueue> beansOfType = applicationContext.getBeansOfType(RedisDelayQueue.class);
        if (MapUtils.isNotEmpty(beansOfType)) {
            log.info("scan redis delay queue size: {}, and this members is [{}]", beansOfType.size(), beansOfType);
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
            ThreadFactory threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("thread-pool-name" + "-%d")
                    .setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
                        log.error("线程池已满，任务被拒绝");
                    })
                    .build();
            // 使用线程池执行任务保证任务不会被用户任务异常终止，每个延时队列对应线程池中一个线程
            executor = new ThreadPoolExecutor(beansOfType.size(), beansOfType.size(), 60, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1), threadFactory, (r, executor1) -> log.error("线程池已满，任务被拒绝"));
            RedisDelayQueueManager.batchRegister(beansOfType);
            for (RedisDelayQueue<?> redisDelayQueue : beansOfType.values()) {
                // 每次重启后先添加一条空数据，避免服务重启后延迟队列take数据阻塞不执行bug（redisson的bug）
                redisDelayQueue.offer(null, 0, TimeUnit.SECONDS);
                executor.submit(redisDelayQueue::consume);
            }
        }
    }

    private void shutdown() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

}
