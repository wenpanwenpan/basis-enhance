package org.clean.code.test;

import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author wenpan 2022/05/18 17:17
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        StringJoiner joiner = new StringJoiner(",");
        System.out.println("====" + joiner.toString());
        System.out.println("".equals(joiner.toString()));
        String classpath = ResourceUtils.getURL("classpath").getPath();
        System.out.println(classpath);

    }

    public List<String> test(String flag) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (flag.equals("hello")) {
                list.add(String.valueOf(i * 11));
            } else {
                list.add(String.valueOf(i));
            }
        }
        return list;
    }
}
