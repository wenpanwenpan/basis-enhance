package org.enhance.data.redis.api.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试线程池
 *
 * @author Mr_wenpan@163.com 2021/08/24 23:45
 */
@Slf4j
@RestController("TestThreadPoolController.v1")
@RequestMapping("/v1/test-thread-pool")
public class TestThreadPoolController {

    @Autowired
    @Qualifier("testThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor testThreadPoolTaskExecutor;

    /**
     * 新建线程池但是不注入容器，JVM关机时这些线程池会不会被关闭，线程池中死循环代码是否会被中断？
     * 结论：当然会
     */
    ScheduledExecutorService register =
            new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
                    .namingPattern("thread-pool-test")
                    .daemon(true)
                    .build());

    ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());

    @GetMapping
    public void testChangeDb() throws InterruptedException {

        AtomicInteger count = new AtomicInteger(0);
        executor.execute(() -> {
            while (count.incrementAndGet() <= 8) {
                System.out.println("执行executor.execute counter = " + count);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    log.error("子线程被中断，异常信息：", e);
                }
            }
        });

        executor.shutdown();
        TimeUnit.SECONDS.sleep(10);
        System.out.println("testChangeDb主线程执行结束.....");

        register.scheduleAtFixedRate(() -> {
            executor.execute(() -> {
                System.out.println("执行executor.execute");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }, 0, 3, TimeUnit.SECONDS);

    }

    @GetMapping("/test01")
    public void testThreadPool01() throws InterruptedException {
        // 请求到达时调用test01方法启动线程池执行任务
        test01();
    }

    public void test01() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        testThreadPoolTaskExecutor.execute(() -> {
            // 子线程运行时间大约18s左右
            while (count.incrementAndGet() <= 8) {
                System.out.println("执行executor.execute counter = " + count);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    log.error("子线程被中断，异常信息：", e);
                }
            }
        });

        // 睡眠一秒后关闭线程池，确保线程池中的任务正在运行
        TimeUnit.SECONDS.sleep(1);
        testThreadPoolTaskExecutor.shutdown();

        // 10s后主线程执行结束
        TimeUnit.SECONDS.sleep(10);
        System.out.println("testChangeDb主线程执行结束.....");
    }
}
