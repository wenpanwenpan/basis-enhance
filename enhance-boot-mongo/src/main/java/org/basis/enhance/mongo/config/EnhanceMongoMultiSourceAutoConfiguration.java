package org.basis.enhance.mongo.config;

import org.basis.enhance.mongo.config.properties.MongoDataSourceProperties;
import org.basis.enhance.mongo.multisource.MonogoMultiDataSourceRegistrar;
import org.basis.enhance.mongo.multisource.listener.ShardingAlgorithmRegisterListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * mongo多数据源自动配置
 *
 * @author Mr_wenpan@163.com 2021/12/20 21:50
 */
@Configuration
@ComponentScan(basePackages = {"org.basis.enhance.mongo.multisource"})
@EnableConfigurationProperties({MongoDataSourceProperties.class})
public class EnhanceMongoMultiSourceAutoConfiguration {

    @Import({MonogoMultiDataSourceRegistrar.class})
    static class ImportMongoMultiDataSourceRegistrar {

    }

    @Bean
    @ConditionalOnProperty(prefix = MongoDataSourceProperties.PREFIX, name = "enable-sharding", havingValue = "true")
    public ShardingAlgorithmRegisterListener shardingAlgorithmRegisterListener(ApplicationContext applicationContext) {
        return new ShardingAlgorithmRegisterListener(applicationContext);
    }
}
