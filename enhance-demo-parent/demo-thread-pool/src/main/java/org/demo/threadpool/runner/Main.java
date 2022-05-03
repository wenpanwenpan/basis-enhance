package org.demo.threadpool.runner;

import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * main
 *
 * @author Mr_wenpan@163.com 2022/04/25 10:07
 */
public class Main {

    public static void main(String[] args) {

        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(1, "xxx");
        map.put(3, "xxx");
        map.put(2, "xxx");
        map.put(100, "xxx");
        map.put(5, "xxx");
        Integer lastKey = map.lastKey();
        System.out.println(lastKey);
        System.out.println(map);
        map.put(4, "hhhh");
        System.out.println(map);

        String name = TestEnum.one.name();
        System.out.println(name);
        String twoName = TestEnum.two.getName();
        String name1 = TestEnum.two.name();
        System.out.println(twoName);
        System.out.println(name1);

        BigDecimal one = new BigDecimal("1");
        BigDecimal two = new BigDecimal("2");

        System.out.println(one.compareTo(two));

        Main main = new Main();
        System.out.println(main.simpleName);


        B b = new B();
        C c = new C();

        b.sayHello();
        c.sayHello();


    }

    String simpleName = getClass().getSimpleName();
}
