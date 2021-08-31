package org.enhance.data.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池注入配置类
 *
 * @author Mr_wenpan@163.com 2021/08/25 21:43
 */
@Configuration
public class ThreadPoolAutoconfiguration {

    @Bean("testThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor testThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor orderThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数量
        orderThreadPoolTaskExecutor.setCorePoolSize(2);
        //最大线程数量
        orderThreadPoolTaskExecutor.setMaxPoolSize(5);
        //队列中最大任务数
        orderThreadPoolTaskExecutor.setQueueCapacity(200);
        //线程名称前缀
        orderThreadPoolTaskExecutor.setThreadNamePrefix("test-thread-pool");
        //线程空闲后最大空闲时间
        orderThreadPoolTaskExecutor.setKeepAliveSeconds(30);
        //当到达最大线程数是如何处理新任务
        orderThreadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return orderThreadPoolTaskExecutor;
    }

}
