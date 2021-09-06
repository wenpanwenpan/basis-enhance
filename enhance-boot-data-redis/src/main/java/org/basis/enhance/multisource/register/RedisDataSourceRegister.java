package org.basis.enhance.multisource.register;

import org.basis.enhance.helper.DynamicRedisHelper;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源注册
 *
 * @author Mr_wenpan@163.com 2021/09/06 10:30
 */
public class RedisDataSourceRegister {

    /**
     * 多数据源redisTemplate注册（不包括默认数据源）
     */
    private static Map<String, RedisTemplate<String, String>> redisTemplateRegister = new ConcurrentHashMap<>();

    /**
     * 多数据源redisHelper注册（不包括默认数据源）
     */
    private static Map<String, DynamicRedisHelper> redisHelperRegister = new ConcurrentHashMap<>();

    public RedisDataSourceRegister() {

    }

    /**
     * 注册RedisTemplate
     */
    public static void redisterRedisTemplate(String name, RedisTemplate<String, String> redisTemplate) {
        if (redisTemplate == null || name == null) {
            return;
        }
        redisTemplateRegister.put(name, redisTemplate);
    }

    /**
     * 注册RedisHelper
     */
    public static void redisterRedisHelper(String name, DynamicRedisHelper redisHelper) {
        if (redisHelper == null || name == null) {
            return;
        }
        redisHelperRegister.put(name, redisHelper);
    }

    /**
     * 获取指定数据源的RedisTemplate
     */
    public static RedisTemplate<String, String> getRedisTemplate(String name) {
        return redisTemplateRegister.get(name);
    }

    /**
     * 获取指定数据源的RedisHelper
     */
    public static DynamicRedisHelper getRedisHelper(String name) {
        return redisHelperRegister.get(name);
    }

    /**
     * 获取多数据源的RedisTemplate注册器
     */
    public static Map<String, RedisTemplate<String, String>> getRedisTemplateRegister() {
        return redisTemplateRegister;
    }

    /**
     * 获取多数据源的RedisHelper注册器
     */
    public static Map<String, DynamicRedisHelper> getRedisHelperRegister() {
        return redisHelperRegister;
    }

}
