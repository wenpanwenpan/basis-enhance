package org.basis.enhance.groovy.annotation;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

/**
 * @author wenpan 2022/09/18 15:06
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("/script/groovy/enhance");
        File[] files = classPathResource.getFile().listFiles();
        if (files == null) {
            System.out.println("没有文件");
            return;
        }
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
            System.out.println(file.getPath());
            System.out.println(file.getName());
            System.out.println(file.getCanonicalPath());
        }
    }
}
