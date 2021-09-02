package org.enhance.data.redis.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.handler.IQueueHandler;
import org.basis.enhance.handler.QueueHandler;

/**
 * redis队列测试service实现
 *
 * @author Mr_wenpan@163.com 2021/08/27 22:42
 */
@Slf4j
@QueueHandler("test-queue")
public class RedisQueueTestServiceImpl implements IQueueHandler {

    @Override
    public void handle(String message) {
        log.info("获取到redis的test-queue队列的消息，消息内容是：{}", message);
        // do something.....
    }
}
