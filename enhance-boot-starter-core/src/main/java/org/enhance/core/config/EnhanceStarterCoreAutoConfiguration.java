package org.enhance.core.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
     * 导入pageHelper自动分页插件配置类
     */
    @Import(PageHelperConfig.class)
    @ConditionalOnClass(SqlSessionFactory.class)
    public static class ImportPageHelperConfig {

    }
}
