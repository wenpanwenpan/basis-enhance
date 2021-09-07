package org.enhance.data.redis.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.helper.RedisHelper;
import org.basis.enhance.multisource.client.RedisMultisourceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.basis.enhance.infra.constant.EnhanceRedisConstants.MultiSource.DEFAULT_SOURCE;

/**
 * 测试动态切换db
 *
 * @author Mr_wenpan@163.com 2021/09/06 22:46
 */
@Slf4j
@RestController("TestChangeDbController.v1")
@RequestMapping("/v1/test-change-db")
public class TestChangeDbController {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired(required = false)
    private RedisMultisourceClient multisourceClient;

    /**
     * 默认数据源切换db测试
     */
    @GetMapping("/test-01")
    public void test01() {
        // 操作1号db
        redisHelper.opsDbOne().opsForValue().set(getRandomValue(), getRandomValue());
        // 操作2号db
        redisHelper.opsDbTwo().opsForValue().set(getRandomValue(), getRandomValue());
        // 操作3号db
        redisHelper.opsDbThree().opsForValue().set(getRandomValue(), getRandomValue());
        // 操作4号db
        redisHelper.opsDbFour().opsForValue().set(getRandomValue(), getRandomValue());
    }

    @GetMapping("/test-02")
    public void test012() {
        try {
            redisHelper.setCurrentDatabase(1);
            redisHelper.lstRightPop("queue");
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 多数据源切换db测试
     */
    @GetMapping("/test-03")
    public void test03() {
        // 操作source1数据源1号db
        multisourceClient.opsDbOne("source1").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source1数据源2号db
        multisourceClient.opsDbTwo("source1").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source1数据源3号db
        multisourceClient.opsDbThree("source1").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source1数据源4号db
        multisourceClient.opsDbFour("source1").opsForValue().set(getRandomValue(), getRandomValue());

        // 操作source2数据源1号db
        multisourceClient.opsDbOne("source2").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source2数据源2号db
        multisourceClient.opsDbTwo("source2").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source2数据源3号db
        multisourceClient.opsDbThree("source2").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source2数据源4号db
        multisourceClient.opsDbFour("source2").opsForValue().set(getRandomValue(), getRandomValue());
    }

    @GetMapping("/test-04")
    public void test04() {
        // 错误测试，操作不存在的数据源
        multisourceClient.opsDbFour("source3").opsForValue().set(getRandomValue(), getRandomValue());
    }

    @GetMapping("/test-05")
    public void test05() {
        // 操作0~15号库以外的db(需要redis上开启多个db)
        multisourceClient.opsOtherDb("source2", 30).opsForValue().set(getRandomValue(), getRandomValue());
    }

    @GetMapping("/test-100")
    public void test100() {
        // 使用多数据源客户端操作默认数据源的指定db

        // 操作默认的数据源的1号db
        multisourceClient.opsDbOne(DEFAULT_SOURCE).opsForValue().set(getRandomValue(), getRandomValue());
        // 操作默认的数据源的2号db
        multisourceClient.opsDbTwo(DEFAULT_SOURCE).opsForValue().set(getRandomValue(), getRandomValue());
        // 操作默认的数据源的3号db
        multisourceClient.opsDbThree(DEFAULT_SOURCE).opsForValue().set(getRandomValue(), getRandomValue());

        // 使用redisHelper操作默认数据源的指定db

        // 操作默认的数据源的1号db
        redisHelper.opsDbOne().opsForValue().get("key");
        // 操作默认的数据源的2号db
        redisHelper.opsDbTwo().opsForValue().get("key");

    }

    private String getRandomValue() {
        return UUID.randomUUID().toString();
    }

}
