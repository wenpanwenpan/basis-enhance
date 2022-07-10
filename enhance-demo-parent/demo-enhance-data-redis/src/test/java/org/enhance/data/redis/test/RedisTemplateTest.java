package org.enhance.data.redis.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * redisTemplate测试
 *
 * @author wenpan 2022/06/26 11:18
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisTemplateTest {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Test
    public void testRedisTemplate() {
        System.out.println("redisTemplate = " + redisTemplate);
    }
}
