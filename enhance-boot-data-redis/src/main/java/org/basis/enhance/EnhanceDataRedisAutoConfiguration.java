package org.basis.enhance;

import org.basis.enhance.config.DynamicRedisTemplateFactory;
import org.basis.enhance.config.properties.StoneRedisProperties;
import org.basis.enhance.helper.DynamicRedisHelper;
import org.basis.enhance.helper.RedisHelper;
import org.basis.enhance.helper.RedisQueueHelper;
import org.basis.enhance.template.DynamicRedisTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * stone-redis自动配置
 * 必须要容器中有RedisConnectionFactory才启动该配置类
 *
 * @author Mr_wenpan@163.com 2021/07/22 22:48
 */
@Configuration
@EnableConfigurationProperties({StoneRedisProperties.class})
@ConditionalOnClass(name = {"org.springframework.data.redis.connection.RedisConnectionFactory"})
public class EnhanceDataRedisAutoConfiguration {

    /**
     * 注入RedisTemplate，key-value都使用string类型
     * RedisConnectionFactory由对应的spring-boot-autoconfigure自动配置到容器
     */
    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        buildRedisTemplate(redisTemplate, redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * 注入StringRedisTemplate，key-value序列化器都使用String序列化器
     */
    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        buildRedisTemplate(redisTemplate, redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * 通过Redis连接工厂构建一个RedisTemplate
     */
    private static void buildRedisTemplate(RedisTemplate<String, String> redisTemplate,
                                           RedisConnectionFactory redisConnectionFactory) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setStringSerializer(stringRedisSerializer);
        redisTemplate.setDefaultSerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
    }

    /**
     * 普通 RedisHelper
     * 关闭动态数据库切换时才注入
     */
    @Bean
    @ConditionalOnProperty(prefix = StoneRedisProperties.PREFIX, name = "dynamic-database", havingValue = "false")
    public RedisHelper redisHelper(StoneRedisProperties redisProperties) {
        return new RedisHelper();
    }

    /**
     * 动态 RedisHelper 配置
     * 开启动态数据库切换时才注入，这里必须以内部类的方式注入，不然启动的时候会导致找不到RedisHelper
     */
    @Configuration
    @ConditionalOnProperty(prefix = StoneRedisProperties.PREFIX, name = "dynamic-database", havingValue = "true", matchIfMissing = true)
    protected static class DynamicRedisAutoConfiguration {

        /**
         * 这些参数由jedis或lettuce客户端帮我们自动配置并注入到容器
         */
        @Bean
        public RedisHelper redisHelper(StringRedisTemplate redisTemplate,
                                       RedisProperties redisProperties,
                                       ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                                       ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
                                       ObjectProvider<JedisClientConfigurationBuilderCustomizer> jedisBuilderCustomizers,
                                       ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers) {

            // 构建动态RedisTemplate工厂
            DynamicRedisTemplateFactory<String, String> dynamicRedisTemplateFactory = new DynamicRedisTemplateFactory<>(
                    redisProperties,
                    sentinelConfiguration,
                    clusterConfiguration,
                    jedisBuilderCustomizers,
                    builderCustomizers
            );
            // ======================================================================================================
            // 这里在注入的时候默认值注入一个默认的redisTemplate，以及将这个redisTemplate放入到map中，该redisTemplate
            // 操作的是配置文件中使用spring.redis.database属性指定的db（若不显示指定，则使用的0号db）
            // 注入的时候值放入这个默认的的redisTemplate到map中，在使用的时候如果需要动态切换库那么会通过连接工厂
            // 重新去创建一个redisTemplate，并缓存到map中（懒加载模式），并不会一次性创建出多个redisTemplate然后缓存起来（连接很昂贵）
            // ======================================================================================================

            DynamicRedisTemplate<String, String> dynamicRedisTemplate = new DynamicRedisTemplate<>(dynamicRedisTemplateFactory);
            // 当不指定库时，默认使用的RedisTemplate来操作Redis
            dynamicRedisTemplate.setDefaultRedisTemplate(redisTemplate);
            Map<Object, RedisTemplate<String, String>> map = new HashMap<>(8);
            // 配置文件中指定使用几号db
            map.put(redisProperties.getDatabase(), redisTemplate);
            // 将redisTemplate缓存起来
            dynamicRedisTemplate.setRedisTemplates(map);

            return new DynamicRedisHelper(dynamicRedisTemplate);
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public RedisQueueHelper redisQueueHelper(RedisHelper redisHelper, StoneRedisProperties redisProperties) {
        return new RedisQueueHelper(redisHelper, redisProperties);
    }

    /**
     * @return Hash 处理类
     */
    @Bean
    public HashOperations<String, String, String> hashOperations(StringRedisTemplate redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * @return String 处理类
     */
    @Bean
    public ValueOperations<String, String> valueOperations(StringRedisTemplate redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * @return List 处理类
     */
    @Bean
    public ListOperations<String, String> listOperations(StringRedisTemplate redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * @return Set 处理类
     */
    @Bean
    public SetOperations<String, String> setOperations(StringRedisTemplate redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * @return ZSet 处理类
     */
    @Bean
    public ZSetOperations<String, String> zSetOperations(StringRedisTemplate redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}
