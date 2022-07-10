package org.enhance.cache.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

/**
 * springboot 缓存测试
 *
 * @author wenpan 2022/06/15 23:16
 */
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
@MapperScan(value = {"org.enhance.cache.demo.mapper"})
@SpringBootApplication
public class SpringbootCacheDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootCacheDemoApplication.class, args);
    }
}
