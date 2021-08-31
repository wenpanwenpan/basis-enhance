package org.enhance.data.redis.app.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池测试
 *
 * @author Mr_wenpan@163.com 2021/08/27 17:15
 */
public class ThreadpoolTest {

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());

    public static void main(String[] args) throws InterruptedException {
//        test01();
        test03();
    }

    /**
     * 测试：不手动调用线程池shutdown方法，在线程池已经没有可运行的任务时JVM关闭会不会关闭线程池（即调用shutdown方法）
     */
    public static void test01() throws InterruptedException {
        // 通过添加钩子函数来阻塞JVM关闭过程，以便于打印出JVM关闭时线程池的状态
        addHook();

        // 提交任务到线程池，这个任务一直不结束并且不响应中断信号，在运行过程中直接关闭JVM
        executor.execute(() -> {
            System.out.println("我是子线程......");
        });

        // 主线程阻塞在这里，等到子线程执行完毕后，我们关闭JVM
        TimeUnit.SECONDS.sleep(30);
    }

    /**
     * 测试：不手动调用线程池shutdown方法，但线程池中有可运行的任务时JVM关闭会不会关闭线程池（即调用shutdown方法）
     */
    public static void test02() throws InterruptedException {
        // 通过添加钩子函数来阻塞JVM关闭过程，以便于打印出JVM关闭时线程池的状态
        addHook();

        // 提交任务到线程池，这个任务一直不结束并且不响应中断信号，在运行过程中直接关闭JVM
        executor.execute(() -> {
            while (true) {
                System.out.println("我是子线程......");
            }
        });

        // 主线程阻塞在这里，等到子线程执行完毕后，我们关闭JVM
        TimeUnit.SECONDS.sleep(30);
    }

    /**
     * 测试：手动调用线程池shutdown方法，但线程池中有可运行的任务时JVM关闭会不会关闭线程池（即调用shutdown方法）
     */
    public static void test03() throws InterruptedException {
        // 通过添加钩子函数来阻塞JVM关闭过程，以便于打印出JVM关闭时线程池的状态
        addHook();

        // 提交任务到线程池，这个任务一直不结束并且不响应中断信号，在运行过程中直接关闭JVM
        executor.execute(() -> {
            while (true) {
                System.out.println("我是子线程......");
            }
        });

        TimeUnit.MILLISECONDS.sleep(200);
        executor.shutdownNow();
        // 主线程阻塞在这里，等到子线程执行完毕后，我们关闭JVM
        TimeUnit.SECONDS.sleep(30);
    }

    /**
     * 添加钩子
     */
    private static void addHook() {
        // 通过添加钩子函数来阻塞JVM关闭过程，以便于打印出JVM关闭时线程池的状态
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("钩子执行，等待 5s JVM正式关闭......");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("钩子函数睡眠被中断......");
            }
            System.out.println("线程池信息 ：" + executor.toString());
        }));
    }

}
