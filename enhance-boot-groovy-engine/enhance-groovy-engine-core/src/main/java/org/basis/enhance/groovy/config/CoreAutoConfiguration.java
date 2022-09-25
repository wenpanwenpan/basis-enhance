package org.basis.enhance.groovy.config;

import org.basis.enhance.groovy.annotation.ConditionalOnExistingProperty;
import org.basis.enhance.groovy.compiler.DynamicCodeCompiler;
import org.basis.enhance.groovy.compiler.impl.GroovyCompiler;
import org.basis.enhance.groovy.config.properties.GroovyEngineProperties;
import org.basis.enhance.groovy.executor.EngineExecutor;
import org.basis.enhance.groovy.executor.impl.DefaultEngineExecutor;
import org.basis.enhance.groovy.filter.GroovyFileNameFilter;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.enhance.groovy.loader.impl.DefaultScriptLoader;
import org.basis.enhance.groovy.registry.ScriptRegistry;
import org.basis.enhance.groovy.registry.impl.DefaultScriptRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FilenameFilter;

/**
 * 核心自动配置类
 *
 * @author wenpan 2022/09/18 18:11
 */
@Configuration
@ConditionalOnExistingProperty(property = "org.enhance.groovy.engine.enable", value = "true")
public class CoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(GroovyEngineProperties.class)
    public GroovyEngineProperties groovyEngineProperties() {
        return new GroovyEngineProperties();
    }

    /**
     * 文件名称过滤器
     */
    @Bean
    @ConditionalOnBean(DefaultScriptLoader.class)
    public FilenameFilter groovyFileNameFilter() {

        return new GroovyFileNameFilter();
    }

    /**
     * groovy class编译器
     */
    @Bean
    @ConditionalOnMissingBean(DynamicCodeCompiler.class)
    public DynamicCodeCompiler groovyCompiler() {

        return new GroovyCompiler();
    }

    /**
     * 脚本加载器
     */
    @Bean
    @ConditionalOnMissingBean(ScriptLoader.class)
    public ScriptLoader scriptLoader(GroovyEngineProperties groovyEngineProperties) {

        return new DefaultScriptLoader(groovyCompiler(), groovyFileNameFilter(), groovyEngineProperties);
    }

    /**
     * 脚本注册中心
     */
    @Bean
    @ConditionalOnMissingBean(ScriptRegistry.class)
    public ScriptRegistry scriptRegistry(GroovyEngineProperties groovyEngineProperties) {

        return new DefaultScriptRegistry(scriptLoader(groovyEngineProperties));
    }

    /**
     * 执行引擎
     */
    @Bean
    @ConditionalOnMissingBean(EngineExecutor.class)
    public EngineExecutor defaultEngineExecutor(GroovyEngineProperties groovyEngineProperties) {

        return new DefaultEngineExecutor(scriptRegistry(groovyEngineProperties));
    }
}
