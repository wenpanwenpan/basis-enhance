package org.enhance.data.redis.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.helper.EasyRedisHelper;
import org.basis.enhance.helper.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 *
 * @author Mr_wenpan@163.com 2021/08/07 18:40
 */
@Slf4j
@RestController("TestEncryptController.v1")
@RequestMapping("/v1/test-enhance-redis")
public class TestEnhanceDataRedisController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Qualifier("redisHelper")
    private RedisHelper redisHelper;

    @GetMapping
    public void testChangeDb() {
        redisHelper.setCurrentDatabase(2);
        // 默认写到1号库
        stringRedisTemplate.opsForValue().set("test-wenpan", "112233");
        // 写到2号库
        redisHelper.strSet("dynamic-key-test-1", "value-1");
        // 写到2号库
        redisHelper.getRedisTemplate().opsForValue().set("hello", "wenpan-test-hello");
        redisHelper.clearCurrentDatabase();
    }

    @GetMapping("/test-2")
    public void testChangeDb2() {
        EasyRedisHelper.execute(2, () -> {
            // 写到2号库
            redisHelper.strSet("dynamic-key-test-2", "value-2");
        });
    }

    @GetMapping("/test-3")
    public void testChangeDb3() {
        EasyRedisHelper.execute(2, redisHelper, (helper) -> {
            // 写到2号库
            helper.strSet("dynamic-key-test-2", "value-2");
        });
    }

    @GetMapping("/test-4")
    public void testChangeDb4() {
        String result = EasyRedisHelper.executeWithResult(2, () -> {
            // 从2号库获取数据
            return redisHelper.strGet("dynamic-key-test-2");
        });
    }

    @GetMapping("/test-5")
    public void testChangeDb5() {
        // 指定操作库，不带返回值的操作，使用redisHelper封装的api
        EasyRedisHelper.execute(2, () -> redisHelper.lstLeftPush("key", "value"));
        // 指定操作库，带返回值的操作，使用redisHelper封装的api
        String result = EasyRedisHelper.executeWithResult(2, () -> redisHelper.strGet("dynamic-key-test-2"));
    }

    @GetMapping("/test-6")
    public void testChangeDb6() {
        // 指定操作库，不带返回值的操作，使用redisTemplate原生api
        EasyRedisHelper.execute(1, (redisTemplate) -> redisTemplate.opsForList().leftPush("key", "value"));
        // 指定操作库，带返回值的操作，使用redisTemplate原生api
        String result = EasyRedisHelper.executeWithResult(1, (redisTemplate) -> redisTemplate.opsForList().leftPop("queue"));
    }

}
