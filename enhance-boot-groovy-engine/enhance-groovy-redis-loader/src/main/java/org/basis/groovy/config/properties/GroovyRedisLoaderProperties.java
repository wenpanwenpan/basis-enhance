package org.basis.groovy.config.properties;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import static org.basis.groovy.config.properties.GroovyRedisLoaderProperties.PREFIX;

/**
 * GroovyRedisLoaderProperties
 *
 * @author wenpan 2022/09/25 13:34
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class GroovyRedisLoaderProperties implements InitializingBean, EnvironmentAware {

    public static final String PREFIX = "enhance.groovy.engine.redis-loader";

    /**
     * 环境信息
     */
    private Environment environment;

    /**
     * 脚本组，以 namespace 来区分不同的应用，同时在Redis里也能够按服务来区分脚本方便查看管理(该值一般和应用名称保持一致即可)
     */
    private String namespace;

    /**
     * 开启基于 Redis 的脚本加载器
     */
    private boolean enable = false;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 如果没有配置namespace，则默认和应用名保持一致
        if (StringUtils.isBlank(namespace)) {
            namespace = environment.getProperty("spring.application.name");
        }
        // 强校验
        if (StringUtils.isBlank(namespace)) {
            throw new UnsupportedOperationException("enhance.groovy.engine.redis-loader.namespace can not be null.");
        }
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
