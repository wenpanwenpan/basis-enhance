package org.enhance.core.demo.config;

import org.enhance.core.annotation.ConditionalOnProperties;
import org.enhance.core.demo.domain.entity.Product;
import org.enhance.core.demo.domain.entity.UserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，主要为了测试自定义注解 {@link ConditionalOnProperties}
 *
 * @author wenpanfeng 2022/09/08 20:09
 */
@Configuration
public class DemoEnhanceStarterCoreConfig {

    @Bean
    @ConditionalOnProperties(prefix = "hello.wenpan",
            properties = {"name", "age", "address"},
            values = {"wenpanx", "128", "北京"},
            anyMatch = true,
            matchIfMissing = true)
    public Product product() {

        return new Product();
    }

    @Bean
    @ConditionalOnProperties(prefix = "hello.wenpan",
            properties = {"name", "age", "address"},
            values = {"wenpanx", "128", "北京"},
            anyMatch = true,
            matchIfMissing = true)
    public UserInfo userInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("lisi");
        return userInfo;
    }
}