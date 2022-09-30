package org.enhance.core.config;

import org.enhance.core.helper.ApplicationContextHelper;
import org.enhance.core.helper.DefaultRequestHelper;
import org.enhance.core.helper.RequestHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置
 *
 * @author Mr_wenpan@163.com 2022/05/03 19:35
 */
@Configuration
public class EnhanceStarterCoreAutoConfiguration {

    /**
     * 注入spring容器助手
     */
    @Bean
    @ConditionalOnMissingBean(ApplicationContextHelper.class)
    public ApplicationContextHelper applicationContextHelper() {

        return new ApplicationContextHelper();
    }

    @Import(TestConfig.class)
    public static class ImportClass {
        
    }

    /**
     * 注入http请求助手
     */
    @Bean
    @ConditionalOnMissingBean(RequestHelper.class)
    public RequestHelper requestHelper() {

        return new DefaultRequestHelper();
    }
}
