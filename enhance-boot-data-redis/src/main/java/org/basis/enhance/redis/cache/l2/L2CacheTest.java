package org.basis.enhance.redis.cache.l2;

import org.apache.commons.lang3.LocaleUtils;
import org.basis.enhance.redis.helper.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * l2缓存测试
 *
 * @author Mr_wenpan@163.com 2021/10/08 18:06
 */
public class L2CacheTest {

    @Autowired
    RedisHelper redisHelper;

    /**
     * 模拟缓存加载器注入到容器中
     */
    CacheLoader<String, String> loader = (key) -> {
        // 从Redis中加载数据
        return redisHelper.strGet(key);
    };
    /**
     * 二级缓存
     */
    L2Cache<String, String> l2Cache = new L2Cache<>(loader);

    public void test() {
        // do something...

        // 从缓存获取(先从2级缓存中获取，如果没有再执行缓存加载器CacheLoader的加载方法去指定的数据源获取数据然后放入到缓存中)
        String result = l2Cache.get("haha");
        System.out.println("result = " + result);

        // do something...

    }

    public static void main(String[] args) {

        // MessageFormat测试
        final String str = "wenpan ni {0} a,hello {1}";
        Object[] arr = new Object[]{"hao", "wenpan"};

        // 获取语言
        Locale locale = LocaleUtils.toLocale("en");
        String format = new MessageFormat(str, locale).format(arr);

        System.out.println("format = " + format);
    }
}
