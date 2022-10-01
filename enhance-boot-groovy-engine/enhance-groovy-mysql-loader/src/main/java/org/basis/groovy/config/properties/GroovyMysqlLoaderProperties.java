package org.basis.groovy.config.properties;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * GroovyRedisLoaderProperties
 *
 * @author wenpan 2022/10/01 16:42
 */
@Data
@ConfigurationProperties(prefix = GroovyMysqlLoaderProperties.PREFIX)
public class GroovyMysqlLoaderProperties implements InitializingBean {

    public static final String PREFIX = "enhance.groovy.engine.mysql-loader";

    /**
     * 命名空间，一般跟应用名保持一致即可
     */
    private String namespace;

    /**
     * 开启基于 MySQL 的脚本加载器
     */
    private boolean enable = false;

    @Override
    public void afterPropertiesSet() {
        // 强校验
        if (StringUtils.isBlank(namespace)) {
            throw new UnsupportedOperationException("enhance.groovy.engine.mysql-loader.namespace can not be null.");
        }
    }
}
