package org.basis.groovy.classpath.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * GroovyClasspathLoaderProperties
 *
 * @author wenpan 2022/09/25 13:34
 */
@Data
@ConfigurationProperties(prefix = GroovyClasspathLoaderProperties.PREFIX)
public class GroovyClasspathLoaderProperties {

    public static final String PREFIX = "enhance.groovy.engine.classpath-loader";

    /**
     * 读取groovy脚本的目录
     */
    private String directory;

    /**
     * 开启基于 classpath 的脚本加载器
     */
    private boolean enable = false;

}
