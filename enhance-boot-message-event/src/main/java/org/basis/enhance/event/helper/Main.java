package org.basis.enhance.event.helper;

import org.basis.enhance.event.event.DefaultMessageEvent;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author Mr_wenpan@163.com 2022/05/02 10:59
 */
public class Main {

    public static void main(String[] args) {
        boolean flg = GenericHelper.supperClassGeneric(DefaultMessageEvent.class, String.class);
        Type[] types = GenericHelper.supperClassGeneric(DefaultMessageEvent.class);
        System.out.println(flg);
        System.out.println(Arrays.toString(types));
        System.out.println(types[0] == String.class);
    }

    static class Test {

    }
}
