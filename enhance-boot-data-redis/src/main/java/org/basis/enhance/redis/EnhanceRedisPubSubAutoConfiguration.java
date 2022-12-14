package org.basis.enhance.redis;

import org.basis.enhance.redis.config.properties.StoneRedisProperties;
import org.basis.enhance.redis.pubsub.PubSubExecutor;
import org.basis.enhance.redis.pubsub.PubSubListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * redis发布订阅相关自动配置，必须要容器中有RedisConnectionFactory才启动该配置类
 *
 * @author Mr_wenpan@163.com 2021/08/30 14:51
 */
@Configuration
@ConditionalOnClass(name = {"org.springframework.data.redis.connection.RedisConnectionFactory"})
@ConditionalOnProperty(value = StoneRedisProperties.PREFIX + ".redis-pub-sub", havingValue = "true")
public class EnhanceRedisPubSubAutoConfiguration {

    /**
     * 发布订阅队列监听异步线程池
     */
    @Bean("queue-listener-executor")
    public AsyncTaskExecutor asyncTaskExecutor(StoneRedisProperties redisProperties) {
        StoneRedisProperties.ThreadPoolProperties threadPoolProperties = redisProperties.getThreadPoolProperties();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setAllowCoreThreadTimeOut(threadPoolProperties.isAllowCoreThreadTimeOut());
        executor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    public PubSubListener queueMessageListener() {
        return new PubSubListener();
    }

    /**
     * 消息监听器适配器，绑定消息处理器，利用反射调用消息处理器
     */
    @Bean("stone-queue-adapter")
    public MessageListenerAdapter listenerAdapter(PubSubListener listener) {
        // 给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“messageListener”
        return new MessageListenerAdapter(listener, "messageListener");
    }

    /**
     * redis消息监听器容器（基于Redis的发布订阅模式）
     */
    @Bean("stone-queue-container")
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   @Qualifier("stone-queue-adapter") MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // todo 这里还需要设置redisSubscriptionExecutor和线程池，否则当大量发布消息到达时可能出现线程问题：
        //      参考 https://www.cnblogs.com/aoeiuv/p/9565617.html
        // 订阅通道stone-redis-queue，这里只订阅一个通道(可以传入list集合订阅多个channel)
        container.addMessageListener(listenerAdapter, new PatternTopic(PubSubListener.CHANNEL));
        return container;
    }

//    @Bean("stone-queue-container")
//    public RedisMessageListenerContainer containerx(RedisConnectionFactory connectionFactory,
//                                                    @Qualifier("stone-queue-adapter") MessageListenerAdapter listenerAdapter,
//                                                    ObjectProvider<Executor> redisSubscriptionExecutor,
//                                                    ObjectProvider<Executor> redisTaskExecutor) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        Executor subscriptionExecutor = redisSubscriptionExecutor.getIfAvailable();
//        Executor taskExecutor = redisTaskExecutor.getIfAvailable();
//        if (Objects.nonNull(subscriptionExecutor)) {
//            container.setSubscriptionExecutor(subscriptionExecutor);
//        }
//        if (Objects.nonNull(taskExecutor)) {
//            container.setTaskExecutor(taskExecutor);
//        }
//        // 订阅通道stone-redis-queue，这里只订阅一个通道(可以传入list集合订阅多个channel)
//        container.addMessageListener(listenerAdapter, new PatternTopic(PubSubListener.CHANNEL));
//        return container;
//    }

    @Bean
    @ConditionalOnMissingBean(PubSubExecutor.class)
    public PubSubExecutor pubSubExecutor() {
        return (message) -> {
            System.out.println("message = " + message);
        };
    }

}
