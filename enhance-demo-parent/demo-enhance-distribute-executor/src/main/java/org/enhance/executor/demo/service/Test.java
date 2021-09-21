package org.enhance.executor.demo.service;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr_wenpan@163.com 2021/09/03 15:51
 */
public class Test {

//    static Set<String> set = Collections.synchronizedSet(new HashSet<>());
//    static Set<String> set = new HashSet<>();

    /**
     * 使用加锁的同步容器也会存在这个问题（比如上面的两种），迭代器遍历也会出现这个问题，但是使用写时复制容器就可以规避这个问题
     */
    static CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();

    public static void main(String[] args) throws InterruptedException {

        // 同步容器一边遍历一边修改并发问题复现
        new Thread(() -> {
            while (true) {
                set.add(UUID.randomUUID().toString());
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
            TimeUnit.MILLISECONDS.sleep(10);
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String str = iterator.next();
                System.out.print("遍历str = " + str + " , ");
            }
//            for (String str : set) {
//                System.out.print("遍历str = " + str + " , ");
//            }
            System.out.println();
        }

    }
}
