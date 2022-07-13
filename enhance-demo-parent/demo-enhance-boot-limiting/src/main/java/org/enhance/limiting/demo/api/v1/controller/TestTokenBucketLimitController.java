package org.enhance.limiting.demo.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.limit.annotation.TokenBucketLimit;
import org.basis.enhance.limit.helper.RedisLimitHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 测试令牌桶限流controller
 *
 * @author wenpan 2022/07/20 21:01
 */
@Slf4j
@RestController
@RequestMapping("/v1/test-bucket")
public class TestTokenBucketLimitController {

    @Autowired
    RedisLimitHelper redisLimitHelper;

    @GetMapping("/test-01")
    public String test01(String limitKey,
                         Integer capacity,
                         Integer permits,
                         Double rate,
                         Integer loop,
                         Integer sleepTime) throws InterruptedException {
        for (int i = 0; i < loop; i++) {
            TimeUnit.MILLISECONDS.sleep(sleepTime);
            // 桶的容量为10，每秒流入1个令牌，每次获取一个令牌
            Boolean limit = redisLimitHelper.tokenLimit(limitKey, capacity, permits, rate);
            if (limit) {
                log.info("[{}] pass.", i);
            } else {
                log.error("[{}] can not pass.", i);
            }
        }
        return "success";
    }

    /**
     * 使用注解进行令牌桶限流
     */
    @TokenBucketLimit
    @GetMapping("/test-annotation")
    public String testAnnotation() {
        log.info("请求没有被限流...");
        return "success";
    }
}
