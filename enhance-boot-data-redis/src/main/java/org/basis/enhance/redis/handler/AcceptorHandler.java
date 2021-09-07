package org.basis.enhance.redis.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.basis.enhance.redis.config.properties.StoneRedisProperties;
import org.basis.enhance.redis.helper.ApplicationContextHelper;
import org.basis.enhance.redis.helper.RedisQueueHelper;
import org.basis.enhance.redis.infra.constant.EnhanceRedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 接收器处理
 * 通过 CommandLineRunner + ScheduledExecutorService 定期的去拉取Redis队列的数据进行消费
 * 缺点（待优化）：
 * ①、过快的轮询频率导致CPU负载较高
 * ②、过慢的轮询频率大并发场景下导致队列消息积压
 * ③、不停机的情况下，如果想动态的关闭数据拉取，不能实现
 * ④、一个队列只允许一个类去消费，不能允许多个类进行消费（也就是不支持类似于发布订阅模式（但是也不是问题，所以忽略））
 *
 * @author Mr_wenpan@163.com 2021/08/20 22:15
 */
public class AcceptorHandler implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptorHandler.class);

    private final StoneRedisProperties redisProperties;
    private final RedisQueueHelper redisQueueHelper;

    public AcceptorHandler(StoneRedisProperties redisProperties, RedisQueueHelper redisQueueHelper) {
        this.redisProperties = redisProperties;
        this.redisQueueHelper = redisQueueHelper;
    }

    @Override
    public void run(String... args) throws Exception {
        // 必须要开启Redis队列
        if (redisProperties != null && !redisProperties.isRedisQueue()) {
            return;
        }
        boolean flag = scanQueueHandler();
        // 若扫描到队列消费类，则开启消费线程
        if (flag) {
            // 启动线程执行消费
            ScheduledExecutorService register =
                    new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
                            .namingPattern("redis-queue-consumer")
                            .daemon(true)
                            .build());
            // 定时任务线程池定时扫描队列
            register.scheduleAtFixedRate(this::scanRedisQueues, EnhanceRedisConstants.Digital.ZERO,
                    Objects.isNull(redisProperties) ? EnhanceRedisConstants.Digital.FIVE : redisProperties.getIntervals(),
                    TimeUnit.SECONDS);
        }
    }

    private void scanRedisQueues() {
        // 获取订阅的所有队列名称
        Set<String> keys = HandlerRepository.getKeySet();
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        // 通过订阅的队列名称获取到该队列对应的线程池，使用对应的线程池执行消息扫描
        keys.forEach(key -> HandlerRepository.getThreadPool(key).execute(new Consumer(key, redisQueueHelper)));
    }

    /**
     * 扫描订阅Redis队列的bean
     */
    private boolean scanQueueHandler() {
        boolean flag = false;
        // 获取所有标注了@QueueHandler注解的bean（key: bean的名称，value：bean对象）
        Map<String, Object> map = ApplicationContextHelper.getContext().getBeansWithAnnotation(QueueHandler.class);
        for (Object service : map.values()) {
            if (service instanceof IQueueHandler || service instanceof IBatchQueueHandler) {
                QueueHandler queueHandler = ProxyUtils.getUserClass(service).getAnnotation(QueueHandler.class);
                if (ObjectUtils.isEmpty(queueHandler)) {
                    LOGGER.debug("could not get target bean , queueHandler : {}", service);
                    continue;
                }
                HandlerRepository.addHandler(queueHandler.value(), service);
                LOGGER.info("Start listening to the redis queue : {}", queueHandler.value());
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 消费线程
     */
    static class Consumer implements Runnable {

        private final String key;
        private final RedisQueueHelper redisQueueHelper;

        public Consumer(String key, RedisQueueHelper redisQueueHelper) {
            this.key = key;
            this.redisQueueHelper = redisQueueHelper;
        }

        @Override
        public void run() {
            // 根据订阅的队列名称获取对应的处理handler
            Object handler = HandlerRepository.getHandler(key);
            if (handler == null) {
                return;
            }
            // 如果这个handler是单个处理的
            if (handler instanceof IQueueHandler) {
                while (true) {
                    String message = redisQueueHelper.pop(true, key);
                    if (StringUtils.isBlank(message)) {
                        return;
                    }
                    ((IQueueHandler) handler).handle(message);
                }
            } else if (handler instanceof IBatchQueueHandler) {
                IBatchQueueHandler batchQueueHandler = (IBatchQueueHandler) handler;
                int size = batchQueueHandler.getSize();
                if (size <= 0) {
                    batchQueueHandler.handle(redisQueueHelper.popAll(key));
                } else {
                    while (true) {
                        List<String> list = redisQueueHelper.popAll(true, key, size);
                        if (CollectionUtils.isEmpty(list)) {
                            return;
                        }
                        batchQueueHandler.handle(list);
                    }
                }
            }
        }
    }

}
