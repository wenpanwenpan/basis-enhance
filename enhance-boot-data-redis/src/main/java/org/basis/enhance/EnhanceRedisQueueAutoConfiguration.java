package org.basis.enhance;

import org.basis.enhance.config.properties.StoneRedisProperties;
import org.basis.enhance.handler.AcceptorHandler;
import org.basis.enhance.helper.QueueMessageListener;
import org.basis.enhance.helper.RedisQueueHelper;
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
 * redis队列相关自动配置，必须要容器中有RedisConnectionFactory才启动该配置类
 *
 * @author Mr_wenpan@163.com 2021/08/30 14:51
 */
@Configuration
@ConditionalOnClass(name = {"org.springframework.data.redis.connection.RedisConnectionFactory"})
public class EnhanceRedisQueueAutoConfiguration {

    /**
     * 队列任务接收
     */
    @Bean
    @ConditionalOnMissingBean
    public AcceptorHandler handlerInit(RedisQueueHelper redisQueueHelper, StoneRedisProperties redisProperties) {
        return new AcceptorHandler(redisProperties, redisQueueHelper);
    }

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
    public QueueMessageListener queueMessageListener() {
        return new QueueMessageListener();
    }

    /**
     * 消息监听器适配器，绑定消息处理器，利用反射调用消息处理器
     */
    @Bean("stone-queue-adapter")
    @ConditionalOnProperty(value = StoneRedisProperties.PREFIX + ".redis-pub-sub", havingValue = "true")
    public MessageListenerAdapter listenerAdapter(QueueMessageListener listener) {
        // 给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“messageListener”
        return new MessageListenerAdapter(listener, "messageListener");
    }

    /**
     * redis消息监听器容器（基于Redis的发布订阅模式）
     */
    @Bean("stone-queue-container")
    @ConditionalOnProperty(value = StoneRedisProperties.PREFIX + ".redis-pub-sub", havingValue = "true")
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   @Qualifier("stone-queue-adapter") MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 订阅通道
        container.addMessageListener(listenerAdapter, new PatternTopic(QueueMessageListener.CHANNEL));
        return container;
    }

}
