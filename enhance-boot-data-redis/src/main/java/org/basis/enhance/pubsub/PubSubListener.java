package org.basis.enhance.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.List;

/**
 * Redis发布订阅消息监听器
 *
 * @author Mr_wenpan@163.com 2021/8/30 5:16 下午
 */
@Slf4j
public class PubSubListener {

    public static final String CHANNEL = "stone-redis-queue";

    @Autowired
    @Qualifier("queue-listener-executor")
    private AsyncTaskExecutor taskExecutor;

    @Autowired
    private List<PubSubExecutor> pubSubExecutorList;

    /**
     * 消息队列监听，一旦Redis对应的channel发布消息后，这个方法立即被吊起
     *
     * @param message 订阅的消息
     */
    public void messageListener(String message) {
        for (PubSubExecutor executor : pubSubExecutorList) {
            // 异步处理，进行订阅消息消费
            taskExecutor.execute(() -> executor.execute(message));
        }
    }

}