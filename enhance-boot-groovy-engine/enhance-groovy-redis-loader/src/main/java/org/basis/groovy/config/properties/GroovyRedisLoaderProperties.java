package org.basis.groovy.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.basis.groovy.config.properties.GroovyRedisLoaderProperties.PREFIX;

/**
 * GroovyRedisLoaderProperties
 *
 * @author wenpan 2022/09/25 13:34
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class GroovyRedisLoaderProperties {

    public static final String PREFIX = "enhance.groovy.engine.redis-loader";

    /**
     * 脚本组，以group来区分不同的应用，同时在Redis里也能够按服务来区分脚本方便查看管理
     */
    private String group;

    /**
     * 开启基于 Redis 的脚本加载器
     */
    private boolean enable = false;

}
