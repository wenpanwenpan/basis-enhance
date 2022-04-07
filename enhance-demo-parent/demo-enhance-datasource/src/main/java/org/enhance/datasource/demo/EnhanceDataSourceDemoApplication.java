package org.enhance.datasource.demo;

import org.basis.enhance.datasource.annotation.EnableDynamicDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 增强数据源测试启动类
 *
 * @author Mr_wenpan@163.com 2021/08/19 16:17
 */
@MapperScan("org.enhance.datasource.demo.mapper")
@EnableDynamicDataSource
@SpringBootApplication
public class EnhanceDataSourceDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnhanceDataSourceDemoApplication.class, args);
    }

}
