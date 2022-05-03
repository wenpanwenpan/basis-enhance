package org.enhance.core.test.mockito.stubbing;

/**
 * stubbing service
 *
 * @author Mr_wenpan@163.com 2022/04/17 19:39
 */
public class StubbingService {

    public int getI() {
        System.out.println("执行getI");
        return 10;
    }

    public String getS() {
        System.out.println("执行getS");
        throw new RuntimeException();
    }
}
