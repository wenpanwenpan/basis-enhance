package org.enhance.mongo.demo;

import org.basis.enhance.mongo.annotation.EnableMongoMultiSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author Mr_wenpan@163.com 2021/12/17 11:06
 */
@EnableMongoMultiSource
@SpringBootApplication
public class EnhanceMongoDemoApplication {

    public static void main(String... args) {
        SpringApplication.run(EnhanceMongoDemoApplication.class, args);
    }
}
