package org.enhance.data.redis.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.helper.RedisHelper;
import org.basis.enhance.multisource.client.RedisMultisourceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 测试多数据源
 *
 * @author Mr_wenpan@163.com 2021/09/06 15:19
 */
@Slf4j
@RestController("TestMultiDataSourceController.v1")
@RequestMapping("/v1/test-multi-source")
public class TestMultiDataSourceController {

    @Autowired
    private RedisMultisourceClient multisourceClient;

    @Autowired
    private RedisHelper redisHelper;

    @GetMapping("/test-1")
    public void test01() {
        // 多数据源切换db测试，写入source1的一号db
        String key = "test-" + UUID.randomUUID().toString();
        String value = "value-" + UUID.randomUUID().toString();
        log.info("key = {}, value = {}", key, value);
        multisourceClient.opsDbOne("source1").opsForValue().set(key, value);
    }

    @GetMapping("/test-2")
    public void test02() {
        // 多数据源切换db测试，写入source2的1号db
        String key = "test-" + UUID.randomUUID().toString();
        String value = "value-" + UUID.randomUUID().toString();
        log.info("key = {}, value = {}", key, value);
        multisourceClient.opsDbOne("source2").opsForValue().set(key, value);
    }

    @GetMapping("/test-3")
    public void test03() {
        // 多数据源切换db测试，写入source1的默认db
        String key = "test-" + UUID.randomUUID().toString();
        String value = "value-" + UUID.randomUUID().toString();
        log.info("key = {}, value = {}", key, value);
        multisourceClient.opsDefaultDb("source1").opsForValue().set(key, value);
    }

    @GetMapping("/test-4")
    public void test04() {
        // 多数据源切换db测试，写入source2的默认db
        String key = "test-" + UUID.randomUUID().toString();
        String value = "value-" + UUID.randomUUID().toString();
        log.info("key = {}, value = {}", key, value);
        multisourceClient.opsDefaultDb("source2").opsForValue().set(key, value);
    }

    /**
     * 开启多数据源的情况下，使用默认数据源并且切换db
     */
    @GetMapping("/test-5")
    public void test05() {
        try {
            redisHelper.setCurrentDatabase(1);
            // 默认数据源的1号db
            redisHelper.getRedisTemplate().opsForValue().set("default-source-change-db-key", "value");
        } finally {
            redisHelper.clearCurrentDatabase();
        }
//
//        EasyRedisHelper.execute(1, () -> {
//            redisHelper.lstRightPop("test-queue");
//        });
    }

    /**
     * 开启多数据源的情况下，使用默认数据源并且使用默认db
     */
    @GetMapping("/test-6")
    public void test06() {
        // 默认数据源的默认db
        redisHelper.getRedisTemplate().opsForValue().set("default-source-default-db-key-1", "value");
    }

}
