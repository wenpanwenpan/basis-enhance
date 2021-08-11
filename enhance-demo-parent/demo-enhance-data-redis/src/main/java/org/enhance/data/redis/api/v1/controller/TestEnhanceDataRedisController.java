package org.enhance.data.redis.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
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

}
