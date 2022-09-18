package org.basis.enhance.groovy.config;

import org.basis.enhance.groovy.annotation.condition.OnExistingPropertyCondition;
import org.basis.enhance.groovy.config.properties.GroovyEngineProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置类
 *
 * @author wenpan 2022/09/18 14:25
 */
@Configuration
@EnableConfigurationProperties(value = {GroovyEngineProperties.class})
public class EnhanceGroovyEngineCoreAutoConfiguration {

    /**
     * 导入CoreAutoConfiguration
     */
    @Import(value = {CoreAutoConfiguration.class})
    static class ImportCoreAutoConfiguration {

    }

    @Bean
    public OnExistingPropertyCondition onExistingPropertyCondition() {

        return new OnExistingPropertyCondition();
    }

}
