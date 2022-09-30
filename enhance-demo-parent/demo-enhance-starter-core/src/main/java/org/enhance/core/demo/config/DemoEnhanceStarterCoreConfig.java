package org.enhance.core.demo.config;

import org.enhance.core.annotation.ConditionalOnProperties;
import org.enhance.core.demo.domain.entity.Product;
import org.enhance.core.demo.domain.entity.UserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
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
            values = {"lisi", "10", "北京"},
            anyMatch = true,
            matchIfMissing = false)
    public UserInfo userInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("lisi");
        return userInfo;
    }
}