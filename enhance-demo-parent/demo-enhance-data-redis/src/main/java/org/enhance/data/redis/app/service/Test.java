package org.enhance.data.redis.app.service;

import lombok.Data;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mr_wenpan@163.com 2021/08/25 10:22
 */
public class Test {

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());

    public static void main(String[] args) throws InterruptedException {

        // 当jvm退出的时候，这个线程直接被终止，因为我不响应中断，所以并不会有异常产生
//        test05();
        test02();
    }

    public static void test01() throws InterruptedException {
        Thread t1 = new Thread(() -> {
        });
        t1.start();

        t1.join();
        while (true) {
            System.out.println("hello world .........");
        }

    }

    public static void test02() throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger(0);
//        executor.execute(() -> {
//            while (atomicInteger.incrementAndGet() <= 10) {
//                System.out.println("执行executor.execute");
//                try {
//                    TimeUnit.SECONDS.sleep(2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        executor.submit(() -> 1);
        executor.execute(() -> {
        });

        TimeUnit.SECONDS.sleep(3);
        // executor.shutdownNow();
        System.out.println("主线程结束......");
    }

    public static void test03() throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("执行钩子函数.....");
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                System.out.println("睡眠被中断......");
            }
            System.out.println("钩子函数执行完毕......");
        }));

        AtomicInteger atomicInteger = new AtomicInteger(0);
        executor.execute(() -> {
            while (atomicInteger.incrementAndGet() <= 10) {
                System.out.println("执行executor.execute");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        TimeUnit.SECONDS.sleep(3);
        executor.shutdownNow();
        System.out.println("主线程结束......");
    }

    public static void test04() throws InterruptedException {
        // 提交任务到线程池，这个任务一直不结束并且不响应中断信号，在运行过程中直接关闭JVM
        executor.execute(() -> {
            while (true) {
                System.out.println("我是子线程......");
            }
        });
        // 确保子线程任务被执行时主线程才退出
        TimeUnit.SECONDS.sleep(1);
        System.out.println("主线程结束......");
    }

    public static void test05() throws InterruptedException {
        Info info = new Info();
        info.setAge(11);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("执行钩子函数.....");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("钩子函数睡眠被中断......");
            }
            System.out.println("钩子函数执行完毕......");
            System.out.println("线程池 ：" + executor.toString());
        }));
        // 提交任务到线程池，这个任务一直不结束并且不响应中断信号，在运行过程中直接关闭JVM
        executor.execute(() -> {
            System.out.println("我是子线程......");
            try {
                TimeUnit.SECONDS.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("当前线程被中断，当前线程中断标记：" + Thread.currentThread().isInterrupted());
                e.printStackTrace();
            }
        });
        TimeUnit.SECONDS.sleep(30);
        info.setName("wenpan");
//        executor.shutdownNow();
    }

    @Data
    static class Info {
        String name;
        Integer age;

        @Override
        protected void finalize() throws Throwable {
            System.out.println("info 对象的finalize方法被吊起");
        }
    }
}
