package org.enhance.core.config;

import org.enhance.core.helper.ApplicationContextHelper;
import org.enhance.core.helper.DefaultRequestHelper;
import org.enhance.core.helper.RequestHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        System.out.println("EnhanceStarterCoreAutoConfiguration注入ApplicationContextHelper......");
        return new ApplicationContextHelper();
    }

    /**
     * 注入http请求助手
     */
    @Bean
    @ConditionalOnMissingBean(RequestHelper.class)
    public RequestHelper requestHelper() {
        System.out.println("EnhanceStarterCoreAutoConfiguration注入RequestHelper......");
        return new DefaultRequestHelper();
    }
}
