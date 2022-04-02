package org.basis.enhance.event.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息事件异步线程池properties
 *
 * @author Mr_wenpan@163.com 2022/04/01 23:20
 */
@ConfigurationProperties(prefix = "basis.message.executor")
public class MessageEventTaskExecutorProperties {

    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;

    private String namePrefix;

    private int keepAliveSeconds;

    public MessageEventTaskExecutorProperties() {
        corePoolSize = 2;
        maxPoolSize = 5;
        queueCapacity = 1024;
        namePrefix = "basis-message-event-executor-";
        keepAliveSeconds = 60;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }
}
