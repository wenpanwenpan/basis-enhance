package org.enhance.limiting.demo.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.limit.helper.RedisLimitHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 测试滑动时间窗口限流controller
 *
 * @author wenpan 2021/07/20 20:58
 */
@Slf4j
@RestController
@RequestMapping("/v1/test-window")
public class TestSlideWindowLimitController {

    @Autowired
    private RedisLimitHelper redisLimitHelper;

    @GetMapping("/test-01")
    public String test01(String limitKey, Integer maxRequest, Integer windowLength, Integer loop, Integer sleepTime) throws InterruptedException {
        for (int i = 0; i < loop; i++) {
            TimeUnit.MILLISECONDS.sleep(sleepTime);
            Boolean pass = redisLimitHelper.windowLimit(limitKey, maxRequest, windowLength);
            if (pass) {
                log.info("[{}] window pass.", i);
            } else {
                log.error("[{}] window not pass.", i);
            }
        }
        return "success";
    }
}
