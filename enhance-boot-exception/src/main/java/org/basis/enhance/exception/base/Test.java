package org.basis.enhance.exception.base;

import java.util.Optional;

/**
 * @author Mr_wenpan@163.com 2021/10/10 11:23
 */
public class Test {

    public static void main(String[] args) {
        String test = null;
        String str = Optional.ofNullable(test).orElse("1000");
        System.out.println(str);
    }
}
