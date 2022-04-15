package org.enhance.core.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 增强web常用工具启动类
 *
 * @author Mr_wenpan@163.com 2021/08/19 16:17
 */
@MapperScan("org.enhance.core.demo.infra.mapper")
@SpringBootApplication
public class EnhanceStarterCoreDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnhanceStarterCoreDemoApplication.class, args);
    }

}