package org.basis.groovy.config;

import org.basis.enhance.groovy.annotation.ConditionalOnExistingProperty;
import org.basis.enhance.groovy.compiler.DynamicCodeCompiler;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.groovy.config.properties.GroovyRedisLoaderProperties;
import org.basis.groovy.loader.RedisScriptLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 自动配置类
 *
 * @author wenpan 2022/09/25 13:12
 */
@Configuration
@EnableConfigurationProperties(value = {GroovyRedisLoaderProperties.class})
public class GroovyRedisLoaderAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * <p>
     * 注册基于Redis的ScriptLoader，配置里必须要显示开启该加载器时才注入 {@code enhance.groovy.engine.redis-loader.enable}
     * 需要依赖于RedisTemplate，所以项目里必须要配置redis
     * </p>
     */
    @Bean
    @ConditionalOnExistingProperty(property = GroovyRedisLoaderProperties.PREFIX + ".enable", value = "true")
    public ScriptLoader redisScriptLoader(RedisTemplate<String, String> redisTemplate,
                                          DynamicCodeCompiler dynamicCodeCompiler,
                                          GroovyRedisLoaderProperties groovyRedisLoaderProperties) {
        logger.info("loading ScriptLoader type is [{}]", RedisScriptLoader.class);
        return new RedisScriptLoader(redisTemplate, dynamicCodeCompiler, groovyRedisLoaderProperties);
    }

}
