package org.basis.enhance.helper;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.config.properties.StoneRedisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * 队列消息监听器
 *
 * @author Mr_wenpan@163.com 2021/8/30 5:16 下午
 */
@Slf4j
public class QueueMessageListener {

    public static final String CHANNEL = "stone-redis-queue";

    @Autowired
    private RedisQueueHelper redisQueueHelper;
    @Autowired
    private StoneRedisProperties redisProperties;
    @Autowired
    @Qualifier("queue-listener-executor")
    private AsyncTaskExecutor taskExecutor;

    /**
     * 消息队列监听
     *
     * @param key 消息队列的key
     */
    public void messageListener(String key) {
        // 异步处理，进行队列消费
        taskExecutor.execute(new Consumer(key));
    }

    class Consumer implements Runnable {

        private String key;

        public Consumer(String key) {
            this.key = key;
        }

        @Override
        public void run() {
            System.out.println("发布订阅响应...，key = " + key);
            String message = redisQueueHelper.pop(true, key);
            log.info("收到了订阅消息，message = {}", message);
        }
    }
}