package org.basis.enhance.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.basis.enhance.mybatis.interceptor.AutoIdInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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

    /**
     * 导入pageHelper自动分页插件配置类
     */
    @Import(PageHelperConfig.class)
    @ConditionalOnClass(SqlSessionFactory.class)
    public static class ImportPageHelperConfig {

    }
}
