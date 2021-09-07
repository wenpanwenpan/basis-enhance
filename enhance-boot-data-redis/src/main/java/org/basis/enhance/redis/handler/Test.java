package org.basis.enhance.redis.handler;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mr_wenpan@163.com 2021/08/24 21:48
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {

        HashMap<String, String> map = new HashMap<>();
        Collection<String> values = map.values();
        System.out.println(values.size());
//        List<String> list = new ArrayList<>(null);
//        System.out.println("======>>>>>" + list.get(0));
    }

    public static void test01() throws InterruptedException {
        ScheduledExecutorService register =
                new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
                        .namingPattern("redis-queue-consumer")
                        .daemon(true)
                        .build());
        testThreadPool(register);

        TimeUnit.SECONDS.sleep(10);
        System.out.println("主线程执行结束.....");
        register.shutdown();
    }

    public static void testThreadPool(ScheduledExecutorService register) {
        register.scheduleAtFixedRate(new Listener(), 0, 3, TimeUnit.SECONDS);
    }

    static class Listener implements Runnable {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());

        @Override
        public void run() {
            System.out.println("执行Listener.run");
            executor.execute(() -> {
                System.out.println("执行executor.execute");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void test2() throws InterruptedException {
        // 新建线程池，只允许一个线程存在，其余的抛弃
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());
        AtomicInteger count = new AtomicInteger(0);
        executor.execute(() -> {
            while (true) {
                System.out.println("子线程执行任务......count = " + count);
                if (count.incrementAndGet() == 3) {
                    System.out.println("即将抛出异常.....");
                    final int i = 1 / 0;
                    System.out.println("抛出异常后.....");
                }
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("主线程提交任务完毕了....");
        TimeUnit.SECONDS.sleep(8);

        executor.execute(() -> System.out.println("想不到吧我还在执行......"));
    }
}
