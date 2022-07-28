package org.hystrix.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * hystrix测试启动类
 *
 * @author wenpanfeng 2022/07/14 10:04
 */
@SpringBootApplication
@EnableConfigurationProperties
public class HystrixDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(HystrixDemoApplication.class, args);
    }
}