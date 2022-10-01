package org.basis.groovy.config.properties;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.basis.groovy.config.properties.GroovyRedisLoaderProperties.PREFIX;

/**
 * GroovyRedisLoaderProperties
 *
 * @author wenpan 2022/09/25 13:34
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class GroovyRedisLoaderProperties implements InitializingBean {

    public static final String PREFIX = "enhance.groovy.engine.redis-loader";

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
        // 强校验
        if (StringUtils.isBlank(namespace)) {
            throw new UnsupportedOperationException("enhance.groovy.engine.redis-loader.namespace can not be null.");
        }
    }
}
