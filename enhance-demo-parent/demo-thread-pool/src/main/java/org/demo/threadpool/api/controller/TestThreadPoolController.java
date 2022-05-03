package org.demo.threadpool.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.demo.threadpool.config.ThreadPoolTestConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 测试线程池
 *
 * @author Mr_wenpan@163.com 2022/04/17 20:34
 */
@Slf4j
@RestController
@RequestMapping("/test-thread-pool")
public class TestThreadPoolController {

    @GetMapping
    public String test() {
        // 提交了长任务到线程池
        ThreadPoolTestConfig.executor.submit(() -> {
            log.info("接收到客户端请求....");
            try {
                TimeUnit.SECONDS.sleep(20);
                log.info("线程任务执行完毕.....");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return "success";
    }

}
