package org.basis.enhance.limit.config;

import org.basis.enhance.limit.helper.RedisHelper;
import org.basis.enhance.limit.helper.RedisLimitHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 基于redis分布式限流自动配置
 *
 * @author wenpanfeng 2021/07/06 23:10
 */
@Configuration
@ConditionalOnClass(RedisConnectionFactory.class)
public class RedisLimitingAutoConfiguration {

    @Bean(value = "redisHelper")
    public RedisHelper redisHelper() {

        return new RedisHelper();
    }

    @Bean(value = "redisLimitHelper")
    public RedisLimitHelper redisLimitHelper(@Qualifier("redisHelper") RedisHelper redisHelper) {

        return new RedisLimitHelper(redisHelper);
    }
}