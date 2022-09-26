package org.basis.groovy.classpath.config;

import org.basis.enhance.groovy.annotation.ConditionalOnExistingProperty;
import org.basis.enhance.groovy.compiler.DynamicCodeCompiler;
import org.basis.enhance.groovy.filter.GroovyFileNameFilter;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.groovy.classpath.config.properties.GroovyClasspathLoaderProperties;
import org.basis.groovy.classpath.loader.ClasspathScriptLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FilenameFilter;

/**
 * <p>
 * 从classpath下加载groovy脚本的loader自动配置类
 * 配置里必须要显示开启该加载器时才注入 {@code enhance.groovy.engine.classpath-loader.enable}
 * </p>
 *
 * @author wenpan 2022/09/25 15:23
 */
@Configuration
@EnableConfigurationProperties(value = {GroovyClasspathLoaderProperties.class})
@ConditionalOnExistingProperty(property = GroovyClasspathLoaderProperties.PREFIX + ".enable", value = "true")
public class GroovyClasspathLoaderAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * <p>
     * 注册基于 classpath 的ScriptLoader
     * </p>
     */
    @Bean
    public ScriptLoader redisScriptLoader(DynamicCodeCompiler dynamicCodeCompiler,
                                          GroovyClasspathLoaderProperties classpathLoaderProperties) {
        logger.info("loading ScriptLoader type is [{}]", ClasspathScriptLoader.class);
        return new ClasspathScriptLoader(dynamicCodeCompiler, groovyFileNameFilter(), classpathLoaderProperties);
    }

    /**
     * 文件名称过滤器
     */
    @Bean
    public FilenameFilter groovyFileNameFilter() {

        return new GroovyFileNameFilter();
    }
}
