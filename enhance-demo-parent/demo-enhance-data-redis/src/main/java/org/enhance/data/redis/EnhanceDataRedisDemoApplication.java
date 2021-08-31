package org.enhance.data.redis;

import org.basis.enhance.multisource.EnableRedisMultiDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 增强spring-data-redis测试启动类
 *
 * @author Mr_wenpan@163.com 2021/08/07 18:09
 */

@SpringBootApplication
@EnableConfigurationProperties
@EnableRedisMultiDataSource
public class EnhanceDataRedisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnhanceDataRedisDemoApplication.class, args);
    }

}
