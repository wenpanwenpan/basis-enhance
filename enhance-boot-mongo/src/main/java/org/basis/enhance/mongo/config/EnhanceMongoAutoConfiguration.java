package org.basis.enhance.mongo.config;

import org.basis.enhance.mongo.config.properties.MongoDataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * mongo增强自动配置
 *
 * @author Mr_wenpan@163.com 2021/12/16 21:18
 */
@Configuration
@EnableConfigurationProperties({MongoDataSourceProperties.class})
public class EnhanceMongoAutoConfiguration {

}
