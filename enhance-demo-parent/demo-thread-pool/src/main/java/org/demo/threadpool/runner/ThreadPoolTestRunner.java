package org.demo.threadpool.runner;

import lombok.extern.slf4j.Slf4j;
import org.demo.threadpool.config.ThreadPoolTestConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 线程池测试runner
 *
 * @author Mr_wenpan@163.com 2022/04/16 19:28
 */
@Slf4j
//@Component
public class ThreadPoolTestRunner implements ApplicationRunner, DisposableBean {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 注册钩子
//        addHook();
        // 单独创建一个线程来跑任务
        Executors.defaultThreadFactory().newThread(this::iRun).start();
    }

    /**
     * 使用不受容器管理的线程池来执行任务
     */
    public void iRun() {
        // 向线程池提交10个任务
        for (int i = 0; i < 10; i++) {
            int j = i;
            ThreadPoolTestConfig.executor.submit(() -> {
                try {
                    log.info("=====>>>>>>>>任务 [{}] 开始执行....", j);
                    TimeUnit.SECONDS.sleep(8);
                    log.info("任务 [{}] 执行完毕....", j);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 使用容器管理的线程池来执行任务
     */
    public void iRun2() {
        ThreadPoolTaskExecutor executorContext = applicationContext.getBean("executorContext", ThreadPoolTaskExecutor.class);
        // 向线程池提交10个任务
        for (int i = 0; i < 10; i++) {
            int j = i;
            executorContext.submit(() -> {
                try {
                    log.info("=====>>>>>>>>任务 [{}] 开始执行....", j);
                    TimeUnit.SECONDS.sleep(8);
                    log.info("任务 [{}] 执行完毕....", j);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * bean被卸载时被吊起
     */
    @Override
    public void destroy() throws Exception {
        log.info(" ThreadPoolTestRunner bean被销毁....");
        ThreadPoolTaskExecutor executorContext = applicationContext.getBean("executorContext", ThreadPoolTaskExecutor.class);
        executorContext.shutdown();
        // 手动关闭线程池
        ThreadPoolTestConfig.executor.shutdown();
    }

    /**
     * 添加钩子，idea可以强制关闭应用，类似于kill -9这样的暴力命令，所以在idea中测试时会发现钩子函数有时不能执行完
     * 接收到操作系统的关闭信号时（非强制关闭信号），会先吊起钩子，然后进行关闭，在spring中钩子函数吊起时，spring中的bean还没有被销毁
     */
    private static void addHook() {
        // 通过添加钩子函数来阻塞JVM关闭过程，以便于打印出JVM关闭时线程池的状态
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("钩子执行，等待 5s JVM正式关闭......");
                TimeUnit.SECONDS.sleep(5);
                log.info("钩子执行完毕......");
            } catch (InterruptedException e) {
                log.info("钩子函数睡眠被中断......");
            }
        }));
    }

}
