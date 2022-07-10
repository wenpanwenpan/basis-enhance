package org.enhance.limiting.demo;

import org.basis.enhance.limit.annotation.EnableRedisLimiting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 增强限流测试启动类
 *
 * @author wenpan 2022/07/22 20:53
 */
@EnableRedisLimiting
@SpringBootApplication
@EnableConfigurationProperties
public class DemoEnhanceLimitApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoEnhanceLimitApplication.class, args);
    }

}
