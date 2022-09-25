package org.basis.enhance.groovy.config;

import org.basis.enhance.groovy.annotation.ConditionalOnExistingProperty;
import org.basis.enhance.groovy.compiler.DynamicCodeCompiler;
import org.basis.enhance.groovy.compiler.impl.GroovyCompiler;
import org.basis.enhance.groovy.config.properties.GroovyEngineProperties;
import org.basis.enhance.groovy.executor.EngineExecutor;
import org.basis.enhance.groovy.executor.impl.DefaultEngineExecutor;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.enhance.groovy.registry.ScriptRegistry;
import org.basis.enhance.groovy.registry.impl.DefaultScriptRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 核心自动配置类 ，配置文件中必须要有 {@code enhance.groovy.engine.enable}配置并且值为true时才开启
 * {@link GroovyEngineProperties#isEnable()}
 * </p>
 *
 * @author wenpan 2022/09/18 18:11
 */
@Configuration
@ConditionalOnExistingProperty(property = GroovyEngineProperties.PREFIX + ".enable", value = "true")
public class CoreAutoConfiguration {

    /**
     * 核心配置映射类
     */
    @Bean
    @ConditionalOnMissingBean(GroovyEngineProperties.class)
    public GroovyEngineProperties groovyEngineProperties() {

        return new GroovyEngineProperties();
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
     * 脚本注册中心，依赖于 ScriptLoader ，ScriptLoader实现类由使用方自由选配
     */
    @Bean
    @ConditionalOnMissingBean(ScriptRegistry.class)
    public ScriptRegistry scriptRegistry(ScriptLoader scriptLoader) {

        return new DefaultScriptRegistry(scriptLoader);
    }

    /**
     * 执行引擎
     */
    @Bean
    @ConditionalOnMissingBean(EngineExecutor.class)
    public EngineExecutor defaultEngineExecutor(ScriptRegistry scriptRegistry) {

        return new DefaultEngineExecutor(scriptRegistry);
    }
}
