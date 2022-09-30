package org.basis.enhance.groovy.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置文件
 *
 * @author wenpan 2022/09/18 15:19
 */
@Setter
@Getter
@ToString
@ConfigurationProperties(prefix = GroovyEngineProperties.PREFIX)
public class GroovyEngineProperties {

    public static final String PREFIX = "enhance.groovy.engine";

    /**
     * 轮询检查脚本变更时间周期，单位：秒
     */
    private Long pollingCycle = 300L;

    /**
     * 初次轮询检查脚本变更延时时间L
     */
    private Long initialDelay = 0L;

    /**
     * 是否开启groovy脚本引擎功能，默认不开启
     */
    private boolean enable = false;

    /**
     * 本地缓存失效时间(单位：分钟)，默认600分钟
     */
    private Long cacheExpireAfterWrite = 600L;

    /**
     * 本地缓存初始容量，默认100
     */
    private Integer cacheInitialCapacity = 100;

    /**
     * 本地缓存最大容量，默认500
     */
    private Long cacheMaximumSize = 500L;
}
