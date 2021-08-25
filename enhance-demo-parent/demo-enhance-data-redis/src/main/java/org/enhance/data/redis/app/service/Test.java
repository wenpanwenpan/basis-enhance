package org.enhance.data.redis.app.service;

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
        TimeUnit.SECONDS.sleep(10);
        System.out.println("主线程结束......");
    }
}
