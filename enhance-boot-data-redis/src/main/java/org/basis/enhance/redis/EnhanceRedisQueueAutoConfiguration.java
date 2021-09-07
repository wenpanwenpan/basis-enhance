package org.basis.enhance.redis;

import org.basis.enhance.redis.config.properties.StoneRedisProperties;
import org.basis.enhance.redis.handler.AcceptorHandler;
import org.basis.enhance.redis.helper.RedisQueueHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redis队列相关自动配置，必须要容器中有RedisConnectionFactory才启动该配置类
 *
 * @author Mr_wenpan@163.com 2021/08/31 14:16
 */
@Configuration
@ConditionalOnClass(name = {"org.springframework.data.redis.connection.RedisConnectionFactory"})
public class EnhanceRedisQueueAutoConfiguration {

    /**
     * 队列任务接收
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StoneRedisProperties.PREFIX + ".redis-queue", havingValue = "true")
    public AcceptorHandler handlerInit(RedisQueueHelper redisQueueHelper, StoneRedisProperties redisProperties) {
        return new AcceptorHandler(redisProperties, redisQueueHelper);
    }

}
