package org.basis.groovy.config;

import org.basis.enhance.groovy.annotation.ConditionalOnExistingProperty;
import org.basis.groovy.config.properties.GroovyMysqlLoaderProperties;
import org.basis.groovy.helper.RegisterScriptToMysqlHelper;
import org.basis.groovy.loader.MysqlScriptLoader;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置类
 *
 * @author wenpan 2022/09/25 13:12
 */
@Configuration
@MapperScan(value = {"org.basis.groovy.mapper"})
@ComponentScan(value = {"org.basis.groovy.repository"})
@EnableConfigurationProperties(value = {GroovyMysqlLoaderProperties.class})
@ConditionalOnExistingProperty(property = GroovyMysqlLoaderProperties.PREFIX + ".enable", value = "true")
public class GroovyMysqlLoaderAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 注入从MySQL里加载脚本的注册器
     */
    @Bean
    @ConditionalOnMissingBean(MysqlScriptLoader.class)
    public MysqlScriptLoader mysqlScriptLoader() {
        logger.info("loading ScriptLoader type is [{}]", MysqlScriptLoader.class);
        return new MysqlScriptLoader();
    }

    @Bean
    @ConditionalOnMissingBean(RegisterScriptToMysqlHelper.class)
    public RegisterScriptToMysqlHelper registerScriptToMysqlHelper() {

        return new RegisterScriptToMysqlHelper();
    }

}
