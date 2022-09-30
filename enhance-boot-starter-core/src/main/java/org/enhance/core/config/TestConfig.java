package org.enhance.core.config;

import org.enhance.core.helper.SpringBeanUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试
 *
 * @author wenpanfeng 2022/09/30 14:40
 */
@Configuration
public class TestConfig {

    @Bean
    public SpringBeanUtil springBeanUtil() {

        return new SpringBeanUtil();
    }

}