package org.enhance.data.redis.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.helper.ApplicationContextHelper;
import org.basis.enhance.helper.EasyRedisHelper;
import org.basis.enhance.helper.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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

    /**
     * 注入默认数据源
     */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 默认数据源对应的redisHelper
     */
    @Autowired
    @Qualifier("redisHelper")
    private RedisHelper redisHelper;

    /**
     * source1数据源对应的redisHelper
     */
    @Autowired(required = false)
    @Qualifier("source1RedisHelper")
    private RedisHelper source1RedisHelper;

    /**
     * source2数据源对应的redisHelper
     */
    @Autowired(required = false)
    @Qualifier("source2RedisHelper")
    private RedisHelper source2RedisHelper;

    /**
     * 注入第一个数据源
     */
    @Autowired(required = false)
    @Qualifier("source1RedisTemplate")
    private RedisTemplate<String, String> source1RedisTemplate;

    /**
     * 注入第二个数据源
     */
    @Autowired(required = false)
    @Qualifier("source1RedisTemplate")
    private RedisTemplate<String, String> source2RedisTemplate;

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

    @GetMapping("/test-7")
    public void testMultiDatasource() {
        // 使用指定的数据源的redisTemplate
        String str1 = redisTemplate.opsForValue().get("wenpan");
        System.out.println("返回值：str1 = " + str1);
        String str2 = source1RedisTemplate.opsForValue().get("wenpan");
        System.out.println("返回值：str2 = " + str2);
        String str3 = source2RedisTemplate.opsForValue().get("wenpan");
        System.out.println("返回值：str3 = " + str3);

        // 获取所有RedisTemplate
        Map<String, RedisTemplate> beansOfType = ApplicationContextHelper.getContext().getBeansOfType(RedisTemplate.class);
        System.out.println("beansOfType = " + beansOfType);
    }

    @GetMapping("/test-8")
    public void testMultiDatasourceRedisHelper() {

        // 默认的数据源的redisHelper
        redisHelper.lstLeftPush("redis-helper-default-key", "redis-helper-default-value");
        // source1数据源对应的redisHelper
        source1RedisHelper.lstLeftPush("redis-helper-db2-key", "redis-helper-db2-value");
        // source2数据源对应的redisHelper
        source2RedisHelper.lstLeftPush("redis-helper-db3-key", "redis-helper-db3-value");

        String rightPop1 = redisHelper.lstRightPop("redis-helper-default-key");
        String rightPop2 = source1RedisHelper.lstRightPop("redis-helper-db2-key");
        String rightPop3 = source2RedisHelper.lstRightPop("redis-helper-db3-key");

        log.info("rightPop1 = {}", rightPop1);
        log.info("rightPop2 = {}", rightPop2);
        log.info("rightPop3 = {}", rightPop3);

        // 获取所有RedisHelper
        Map<String, RedisHelper> beansOfType = ApplicationContextHelper.getContext().getBeansOfType(RedisHelper.class);
        System.out.println("beansOfType = " + beansOfType);
    }

    @GetMapping("/test-default-template")
    public void testDefaultRedisTemplate() {
        // 使用默认数据源的redisTemplate操作默认数据源
        redisTemplate.opsForValue().set("key", "value");
    }

    @GetMapping("/test-default-redisHelper")
    public void testDefaultRedisHelper() {
        // 使用默认数据源的redisHelper操作默认数据源
        redisHelper.lstRightPop("key");
    }

    @GetMapping("/test-source1-template")
    public void testSource1RedisTemplate() {
        // 使用source1数据源的redisTemplate操作source1数据源
        source1RedisTemplate.opsForValue().set("key", "value");
    }

    @GetMapping("/test-source1-redisHelper")
    public void testSource1RedisHelper() {
        // 使用source1数据源的redisHelper操作source1数据源(切换db操作)
        EasyRedisHelper.execute(2, () -> source1RedisHelper.lstLeftPush("key", "value"));
    }
}
