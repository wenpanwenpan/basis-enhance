package org.basis.enhance;

import org.basis.enhance.config.DynamicRedisTemplateFactory;
import org.basis.enhance.config.properties.RedisDataSourceProperties;
import org.basis.enhance.config.properties.StoneRedisProperties;
import org.basis.enhance.helper.ApplicationContextHelper;
import org.basis.enhance.helper.DynamicRedisHelper;
import org.basis.enhance.helper.RedisHelper;
import org.basis.enhance.helper.RedisQueueHelper;
import org.basis.enhance.infra.condition.ConditionalOnExistingProperty;
import org.basis.enhance.infra.condition.ConditionalOnMissingProperty;
import org.basis.enhance.template.DynamicRedisTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * stone-redis自动配置，必须要容器中有RedisConnectionFactory才启动该配置类
 *
 * @author Mr_wenpan@163.com 2021/07/22 22:48
 */
@Configuration
@EnableConfigurationProperties({StoneRedisProperties.class, RedisDataSourceProperties.class})
@ConditionalOnClass(name = {"org.springframework.data.redis.connection.RedisConnectionFactory"})
public class EnhanceDataRedisAutoConfiguration {

    @Bean
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

    /**
     * 导入springboot自动配置中导入的RedisTemplate和StringRedisTemplate
     */
//    @Import(RedisAutoConfiguration.class)
//    @Configuration
//    static class ImportRedisAutoConfiguration {
//
//    }

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
     * 普通 RedisHelper，关闭动态数据库切换或集群模式下时才注入（redis官方要求集群模式下不能切换db只有db0）
     */
    @Primary
    @ConditionalOnStaticRedisHelper
    @Bean(name = {"redisHelper", StoneRedisProperties.DEFAULT_REDIS, StoneRedisProperties.DEFAULT_REDIS_HELPER})
    public RedisHelper redisHelper() {
        return new RedisHelper();
    }

    /**
     * 这些参数由jedis或lettuce客户端帮我们自动配置并注入到容器
     */
    @Primary
    @ConditionalOnDynamicRedisHelper
    @Bean(name = {"redisHelper", StoneRedisProperties.DEFAULT_REDIS, StoneRedisProperties.DEFAULT_REDIS_HELPER})
    public RedisHelper dynamicRedisHelper(StringRedisTemplate redisTemplate,
                                          RedisProperties redisProperties,
                                          ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                                          ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
                                          ObjectProvider<JedisClientConfigurationBuilderCustomizer> jedisBuilderCustomizers,
                                          ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers) {

        // 构建动态RedisTemplate工厂
        DynamicRedisTemplateFactory<String, String> dynamicRedisTemplateFactory =
                new DynamicRedisTemplateFactory<>(redisProperties, sentinelConfiguration,
                        clusterConfiguration, jedisBuilderCustomizers, builderCustomizers);
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

    /**
     * 动态 RedisHelper 配置
     * 开启动态数据库切换时才注入，这里必须以内部类的方式注入，不然启动的时候会导致找不到RedisHelper
     */
//    @Configuration
//    @ConditionalOnProperty(prefix = StoneRedisProperties.PREFIX, name = "dynamic-database", havingValue = "true", matchIfMissing = true)
//    protected static class DynamicRedisAutoConfiguration {
//
//        /**
//         * 这些参数由jedis或lettuce客户端帮我们自动配置并注入到容器
//         */
//        @Bean
//        public RedisHelper redisHelper(StringRedisTemplate redisTemplate,
//                                       RedisProperties redisProperties,
//                                       ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
//                                       ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
//                                       ObjectProvider<JedisClientConfigurationBuilderCustomizer> jedisBuilderCustomizers,
//                                       ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers) {
//
//            // 构建动态RedisTemplate工厂
//            DynamicRedisTemplateFactory<String, String> dynamicRedisTemplateFactory = new DynamicRedisTemplateFactory<>(
//                    redisProperties,
//                    sentinelConfiguration,
//                    clusterConfiguration,
//                    jedisBuilderCustomizers,
//                    builderCustomizers
//            );
//            // ======================================================================================================
//            // 这里在注入的时候默认值注入一个默认的redisTemplate，以及将这个redisTemplate放入到map中，该redisTemplate
//            // 操作的是配置文件中使用spring.redis.database属性指定的db（若不显示指定，则使用的0号db）
//            // 注入的时候值放入这个默认的的redisTemplate到map中，在使用的时候如果需要动态切换库那么会通过连接工厂
//            // 重新去创建一个redisTemplate，并缓存到map中（懒加载模式），并不会一次性创建出多个redisTemplate然后缓存起来（连接很昂贵）
//            // ======================================================================================================
//
//            DynamicRedisTemplate<String, String> dynamicRedisTemplate = new DynamicRedisTemplate<>(dynamicRedisTemplateFactory);
//            // 当不指定库时，默认使用的RedisTemplate来操作Redis
//            dynamicRedisTemplate.setDefaultRedisTemplate(redisTemplate);
//            Map<Object, RedisTemplate<String, String>> map = new HashMap<>(8);
//            // 配置文件中指定使用几号db
//            map.put(redisProperties.getDatabase(), redisTemplate);
//            // 将redisTemplate缓存起来
//            dynamicRedisTemplate.setRedisTemplates(map);
//
//            return new DynamicRedisHelper(dynamicRedisTemplate);
//        }
//
//    }
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

    @Documented
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Conditional(OnDynamicRedisHelperCondition.class)
    @interface ConditionalOnDynamicRedisHelper {
    }

    /**
     * stone.redis.dynamic-database=true 且 非集群模式下，则创建动态的 RedisHelper，可以切换 Redis DB.
     */
    private static class OnDynamicRedisHelperCondition extends AllNestedConditions {

        /**
         * 注册bean时生效
         */
        public OnDynamicRedisHelperCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        /**
         * 开启db动态切换
         */
        @ConditionalOnProperty(prefix = StoneRedisProperties.PREFIX,
                name = "dynamic-database", havingValue = "true", matchIfMissing = true)
        static class OnDynamicRedisHelper {
        }

        /**
         * 非集群配置
         */
        @ConditionalOnMissingProperty("spring.redis.cluster.nodes")
        static class OnSingleCluster {
        }
    }

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Conditional(OnStaticRedisHelperCondition.class)
    @interface ConditionalOnStaticRedisHelper {

    }

    /**
     * stone.redis.dynamic-database=false或者集群模式下，则创建静态的 RedisHelper，禁止切换 Redis db
     */
    private static class OnStaticRedisHelperCondition extends AnyNestedCondition {

        public OnStaticRedisHelperCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        /**
         * 关闭db动态切换
         */
        @ConditionalOnProperty(prefix = StoneRedisProperties.PREFIX, name = "dynamic-database", havingValue = "false")
        static class OnStaticRedisHelper {
        }

        /**
         * 非集群模式
         * 存在spring.redis.cluster.nodes配置(即集群模式禁止切换db，Redis官方规定集群模式下只有db0)
         */
        @ConditionalOnExistingProperty(value = {"spring.redis.cluster.nodes"})
        static class OnRedisCluster {
        }
    }
}
