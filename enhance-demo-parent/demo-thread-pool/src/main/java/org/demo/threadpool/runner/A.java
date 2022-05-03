package org.demo.threadpool.runner;

/**
 * alei
 *
 * @author Mr_wenpan@163.com 2022/04/27 16:23
 */
public abstract class A {

    private final String name = getClass().getSimpleName();


    public void sayHello() {
        System.out.println("====>>>>" + name);
    }
}
