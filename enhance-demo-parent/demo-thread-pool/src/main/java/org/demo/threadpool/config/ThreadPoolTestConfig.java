package org.demo.threadpool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置测试类
 *
 * @author Mr_wenpan@163.com 2022/04/16 19:25
 */
@Component
public class ThreadPoolTestConfig {

    /**
     * 不交给容器管理的线程池
     */
    public static ThreadPoolTaskExecutor executor;

    @PostConstruct
    public void init() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(30);
        // 设置允许核心线程超时，这样当容器关闭后，一旦没有任务并且达到超时时间，线程池会自动销毁
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("my-thread1-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
    }

    /**
     * 交给容器管理的线程池
     */
    @Bean
    public ThreadPoolTaskExecutor executorContext() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(30);
        // 设置允许核心线程超时，这样当容器关闭后，一旦没有任务并且达到超时时间，线程池会自动销毁
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("my-thread2-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 交给容器管理的线程池
     */
    @Bean
    public ThreadPoolTaskExecutor executorContext2() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(30);
        // 设置允许核心线程超时，这样当容器关闭后，一旦没有任务并且达到超时时间，线程池会自动销毁
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("my-thread2-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
