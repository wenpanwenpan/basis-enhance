package org.basis.enhance.mybatis.config;

import org.basis.enhance.mybatis.interceptor.AutoIdInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 增强mybatis自动配置
 *
 * @author Mr_wenpan@163.com 2021/8/13 10:30 下午
 */
@Configuration
public class EnhanceMybatisAutoConfiguration {

    @Bean
    public AutoIdInterceptor autoIdInterceptor() {
        return new AutoIdInterceptor();
    }
}
