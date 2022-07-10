package org.enhance.cache.test;

/**
 * 测试
 *
 * @author wenpan 2022/06/17 14:47
 */
public class Test {

    final long count = 100000L;

    @org.junit.Test
    public void test1() {
        System.out.println(1 << 4);
        long start = System.currentTimeMillis();
        Person person = null;
        for (long i = 0; i < count; i++) {
            person = new Person();
            "xx".equalsIgnoreCase(person.getName());
        }
        System.out.println("test1 = " + (System.currentTimeMillis() - start));
    }

    @org.junit.Test
    public void test2() {
        long start = System.currentTimeMillis();
        for (long i = 0; i < count; i++) {
            Person person = new Person();
            "xx".equalsIgnoreCase(person.getName());
        }
        System.out.println("test2 = " + (System.currentTimeMillis() - start));
    }
}
