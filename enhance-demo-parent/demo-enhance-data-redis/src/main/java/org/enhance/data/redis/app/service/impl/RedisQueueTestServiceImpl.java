package org.enhance.data.redis.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.handler.IQueueHandler;
import org.basis.enhance.handler.QueueHandler;
import org.enhance.data.redis.app.service.RedisQueueTestService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * redis队列测试service实现
 *
 * @author Mr_wenpan@163.com 2021/08/27 22:42
 */
@Slf4j
@Service
@QueueHandler("test-queue")
public class RedisQueueTestServiceImpl implements RedisQueueTestService, IQueueHandler {

    @Override
    public void handle(String message) {
        log.info("message = {}, 回调handle方法.....", message);
        while (true) {
            System.out.println("我还在...");
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
