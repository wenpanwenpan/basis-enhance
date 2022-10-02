package org.basis.groovy.config.properties;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

/**
 * GroovyRedisLoaderProperties
 *
 * @author wenpan 2022/10/01 16:42
 */
@Data
@ConfigurationProperties(prefix = GroovyMysqlLoaderProperties.PREFIX)
public class GroovyMysqlLoaderProperties implements InitializingBean, EnvironmentAware {

    public static final String PREFIX = "enhance.groovy.engine.mysql-loader";

    /**
     * spring 环境信息
     */
    private Environment environment;

    /**
     * 命名空间，一般跟应用名保持一致即可
     */
    private String namespace;

    /**
     * key分隔符(默认以下划线分割)
     */
    private String keySeparator = "_";

    /**
     * 开启基于 MySQL 的脚本加载器
     */
    private boolean enable = false;

    @Override
    public void afterPropertiesSet() {
        // 如果没有配置namespace，则默认和应用名保持一致
        if (StringUtils.isBlank(namespace)) {
            namespace = environment.getProperty("spring.application.name");
        }
        // 强校验
        if (StringUtils.isBlank(namespace)) {
            throw new UnsupportedOperationException("enhance.groovy.engine.mysql-loader.namespace can not be null.");
        }
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
