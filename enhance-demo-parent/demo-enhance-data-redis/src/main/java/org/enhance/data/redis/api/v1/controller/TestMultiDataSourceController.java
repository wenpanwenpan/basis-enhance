package org.enhance.data.redis.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.redis.helper.ApplicationContextHelper;
import org.basis.enhance.redis.helper.RedisHelper;
import org.basis.enhance.redis.infra.constant.EnhanceRedisConstants;
import org.basis.enhance.redis.multisource.RedisMultiDataSourceRegistrarExtension;
import org.basis.enhance.redis.multisource.client.RedisMultiSourceClient;
import org.basis.enhance.redis.multisource.register.RedisDataSourceRegister;
import org.enhance.data.redis.domain.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired(required = false)
    private RedisMultiSourceClient multisourceClient;

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

    @GetMapping("/test-100")
    public void test100() {
        // 不开启动态db切换的情况下，使用multisourceClient强制切换db结果验证（验证结果：这里会抛异常：静态RedisHelper静止切换db）
        String key = "test-" + UUID.randomUUID().toString();
        String value = "value-" + UUID.randomUUID().toString();
        log.info("key = {}, value = {}", key, value);
        multisourceClient.opsDbThree("source1").opsForValue().set(key, value);
    }

    @GetMapping("/test-101")
    public void test101() {
        // 不开启动态db切换的情况下，使用multisourceClient操作默认db验证
        String key = "test-" + UUID.randomUUID().toString();
        String value = "value-" + UUID.randomUUID().toString();
        log.info("key = {}, value = {}", key, value);
        multisourceClient.opsDefaultDb("source1").opsForValue().set(key, value);
    }

    /**
     * 动态新增Redis数据源扩展点使用示例
     */
    @GetMapping(value = "/test-extension")
    public String testMultiDatasourceExtension(String datasourceName) {
        // 模拟读取到了新的Redis配置 RedisProperties
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setHost("wenpan-host");
        redisProperties.setPort(6379);
        redisProperties.setPassword("WenPan@123");
        redisProperties.setDatabase(10);

        // 通过动态数据源扩展点工具类注册一个新的数据源（数据源名称为 datasourceName）
        RedisMultiDataSourceRegistrarExtension.registerRedisDataSource(datasourceName, redisProperties);
        // 测试通过多数据源客户端来获取新增的数据源的RedisTemplate并操作10号db
        RedisTemplate<String, String> redisTemplate = multisourceClient.opsDbTen(datasourceName);
        RedisHelper redisHelper = RedisDataSourceRegister.getRedisHelper(datasourceName + EnhanceRedisConstants.MultiSource.REDIS_HELPER);
        log.info("新增数据源成功，该数据源名称是：{},对应的redisTemplate是：{},redisHelper是：{}", datasourceName, redisTemplate, redisHelper);
        log.info("redisTemplate is : {}", redisTemplate);

        // 多次获取，验证是否是同一个 redisTemplate 和 RedisHelper
        // 多次从容器里获取RedisHelper
        RedisHelper redisHelper1 = ApplicationContextHelper.getContext().getBean(datasourceName + EnhanceRedisConstants.MultiSource.REDIS_HELPER, RedisHelper.class);
        RedisHelper redisHelper2 = ApplicationContextHelper.getContext().getBean(datasourceName + EnhanceRedisConstants.MultiSource.REDIS_HELPER, RedisHelper.class);
        RedisHelper redisHelper3 = ApplicationContextHelper.getContext().getBean(datasourceName + EnhanceRedisConstants.MultiSource.REDIS_HELPER, RedisHelper.class);
        log.info("redisHelper is : {}, redisHelper1 is : {} , redisHelper2 is :{} , redisHelper3 is : {}", redisHelper, redisHelper1, redisHelper2, redisHelper3);

        // 多次从容器里获取RedisTemplate
        RedisTemplate redisTemplate1 = ApplicationContextHelper.getContext().getBean(datasourceName + EnhanceRedisConstants.MultiSource.REDIS_TEMPLATE, RedisTemplate.class);
        RedisTemplate redisTemplate2 = ApplicationContextHelper.getContext().getBean(datasourceName + EnhanceRedisConstants.MultiSource.REDIS_TEMPLATE, RedisTemplate.class);
        RedisTemplate redisTemplate3 = ApplicationContextHelper.getContext().getBean(datasourceName + EnhanceRedisConstants.MultiSource.REDIS_TEMPLATE, RedisTemplate.class);
        // 从RedisDataSourceRegister中获取
        RedisTemplate redisTemplate4 = RedisDataSourceRegister.getRedisTemplate(datasourceName + EnhanceRedisConstants.MultiSource.REDIS_TEMPLATE);

        log.info("redisTemplate1 is : {}, redisTemplate2 is : {}, redisTemplate3 is : {} , redisTemplate4 is : {}", redisTemplate1, redisTemplate2, redisTemplate3, redisTemplate4);

        return "success";
    }

    /**
     * 对新数据源的读写示例
     */
    @GetMapping(value = "/test-new-datasource")
    public String testNewDatasource(String datasourceName, String key, String value) {

        // 获取到对应数据源的RedisTemplate
        RedisTemplate<String, String> redisTemplate = multisourceClient.opsDbTen(datasourceName);

        // 写入key-value
        redisTemplate.opsForValue().set(key, value);

        // 通过key获取value
        String str = redisTemplate.opsForValue().get(key);
        System.out.println("str = " + str);

        // 删除key后再次通过key获取
        redisTemplate.delete(key);
        str = redisTemplate.opsForValue().get(key);
        System.out.println("str = " + str);

        return "success";
    }

    /**
     * 容器启动好后动态向容器里注册单例bean
     */
    @GetMapping("/test-add-bean")
    public void testAddBean(String beanName) {
        ApplicationContextHelper.dynamicRegisterSingleBean(beanName, Order.class);
    }

    /**
     * 容器启动好后动态向容器里注册单例bean
     */
    @GetMapping("/test-add-bean2")
    public void testAddBean2(String beanName) {
        Order order = new Order();
        order.setOrderId(beanName);
        ApplicationContextHelper.dynamicRegisterSingletonBean(beanName, order);
    }

    /**
     * 从容器中通过名称删除bean
     */
    @GetMapping("/test-delete-bean")
    public void testDeleteBean(String beanName) {
        Object bean = ApplicationContextHelper.getContext().getBean(beanName);
        System.out.println(bean);
        ApplicationContextHelper.removeBean(beanName);
        bean = ApplicationContextHelper.getContext().getBean(beanName);
        System.out.println(bean);
    }

}
