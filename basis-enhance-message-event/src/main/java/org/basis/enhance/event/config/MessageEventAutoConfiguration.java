package org.basis.enhance.event.config;

import org.basis.enhance.event.config.properties.MessageEventTaskExecutorProperties;
import org.basis.enhance.event.executor.MessageEventTaskExecutor;
import org.basis.enhance.event.listener.ApplicationListener;
import org.basis.enhance.event.listener.DefaultApplicationListener;
import org.basis.enhance.event.publisher.DefaultEventPublisher;
import org.basis.enhance.event.publisher.EventPublisher;
import org.basis.enhance.event.registrar.ApplicationEventMulticaster;
import org.basis.enhance.event.registrar.impl.DefaultApplicationEventMulticaster;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 消息事件自动配置
 *
 * @author Mr_wenpan@163.com 2022/04/01 18:03
 */
@Configuration
@EnableConfigurationProperties({MessageEventTaskExecutorProperties.class})
public class MessageEventAutoConfiguration {

    @Import(ScanOtherClass.class)
    static class ScanOtherBean {

    }

    @Bean
    @ConditionalOnMissingBean
    public EventPublisher eventPublisher(ApplicationEventMulticaster eventMulticaster,
                                         MessageEventTaskExecutor messageEventTaskExecutor) {

        return new DefaultEventPublisher(eventMulticaster, messageEventTaskExecutor);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationEventMulticaster basisEventMulticaster(Set<ApplicationListener<?>> applicationListeners) {
        ApplicationEventMulticaster applicationEventMulticaster = new DefaultApplicationEventMulticaster();
        applicationEventMulticaster.addApplicationListeners(applicationListeners);
        return applicationEventMulticaster;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultApplicationListener defaultApplicationListener() {
        return new DefaultApplicationListener();
    }

    @Bean
    @ConditionalOnMissingBean
    MessageEventTaskExecutor messageEventTaskExecutor(MessageEventTaskExecutorProperties properties) {
        MessageEventTaskExecutor messageEventTaskExecutor = new MessageEventTaskExecutor();
        //核心线程数量
        messageEventTaskExecutor.setCorePoolSize(properties.getCorePoolSize());
        //最大线程数量
        messageEventTaskExecutor.setMaxPoolSize(properties.getMaxPoolSize());
        //队列中最大任务数
        messageEventTaskExecutor.setQueueCapacity(properties.getQueueCapacity());
        //线程名称前缀
        messageEventTaskExecutor.setThreadNamePrefix(properties.getNamePrefix());
        //线程空闲后最大空闲时间
        messageEventTaskExecutor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        //当到达最大线程数是如何处理新任务
        messageEventTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return messageEventTaskExecutor;
    }
}
