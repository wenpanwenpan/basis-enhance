package org.enhance.exception;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 测试异常增强主启动类
 *
 * @author Mr_wenpan@163.com 2021/10/10 18:05
 */
@SpringBootApplication
@EnableConfigurationProperties
public class EnhanceExceptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnhanceExceptionApplication.class, args);
    }
}
