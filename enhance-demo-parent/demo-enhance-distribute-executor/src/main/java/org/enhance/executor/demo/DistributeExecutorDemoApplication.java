package org.enhance.executor.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author Mr_wenpan@163.com 2021/08/19 16:17
 */
@MapperScan("org.enhance.executor.demo.infra.mapper")
@SpringBootApplication
public class DistributeExecutorDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DistributeExecutorDemoApplication.class, args);
    }
}
