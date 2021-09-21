package org.basis.enhance.executor.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义新建线程池不手动关闭线程池造成的线程泄漏问题演示
 *
 * @author Mr_wenpan@163.com 2021/08/18 21:04
 */
@Slf4j
public class Test {

    public static void main(String[] args) throws InterruptedException {

        ScheduledExecutorService executorService = threadPoolTest02();

        TimeUnit.SECONDS.sleep(10);
        System.out.println("主线程执行结束....");

        // 前面的任务执行出现异常后再次提交任务到线程池
        // ThreadPoolExecutor executor = threadPoolTest01();
//        executor.submit(() -> {
//            log.info("哈哈哈，其实我没有死，想不到吧，只是上一个任务出错了，然后我就把它结束了....");
//        });


        // 手动关闭
//        executor.shutdown();
        executorService.shutdown();
    }

    /**
     * 测试出现异常时ThreadPoolExecutor线程池任务会不会中断
     */
    public static ThreadPoolExecutor threadPoolTest01() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        // 新建线程池，只允许一个线程存在，其余的抛弃
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());
        executor.execute(() -> {

            while (true) {
                int num = atomicInteger.incrementAndGet();
                if (num == 4) {
                    System.out.println("出现异常....");
                    final int j = 1 / 0;
                    System.out.println("出现异常之后");
                }

                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("线程池测试.....");
            }
        });

        return executor;
    }

    /**
     * 测试出现异常时ScheduledThreadPoolExecutor线程池任务会不会中断
     * 结论：当出现异常时如果不捕获异常那么该线程任务不会继续执行，并且线程没有死掉，在JVM关机时也不会关闭（.daemon(true)守护线程除外）
     */
    public static ScheduledExecutorService threadPoolTest02() throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        ScheduledExecutorService register = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
                .namingPattern("thread-name")
                //.daemon(true)
                .build());

        register.scheduleAtFixedRate(() -> {
            int num = atomicInteger.incrementAndGet();
            if (num == 4) {
                log.error("定时任务线程池执行第一个任务出现异常啦......");
                final int j = 1 / 0;
                log.error("定时任务线程池执行第一个任务出现异常之后......");
            }
            log.info("定时任务线程池执行第一个任务......");
        }, 0, 1, TimeUnit.SECONDS);

        // 上面的任务出现异常后这里休眠1s再执行其他任务
        TimeUnit.SECONDS.sleep(1);

        register.scheduleAtFixedRate(() -> {
            log.info("哈哈，定时任务线程池执行第二个任务啦....");
        }, 0, 1, TimeUnit.SECONDS);
        return register;
    }

    /**
     * 测试手动new的线程池（ThreadPoolTaskExecutor）当JVM关闭时线程池会不会关闭（即不被spring管理的线程池）
     * 结论：不会。JVM关闭时必须手动调用shutdown关闭
     */
    public static void threadPoolTest03() throws InterruptedException {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(1);
        executor.setCorePoolSize(1);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        executor.submit(() -> {
            while (true) {
                log.info("线程打印.....");
                TimeUnit.SECONDS.sleep(2);
            }
        });

        TimeUnit.SECONDS.sleep(10);

        // 手动关闭
        executor.shutdown();

    }
}
