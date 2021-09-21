package org.basis.enhance.executor.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 执行器节点配置
 *
 * @author Mr_wenpan@163.com 2021/8/08 3:23 下午
 */
@ConfigurationProperties(
        prefix = "stone.executor"
)
@Data
public class ExecutorProperties {

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * redis实例key
     */
    private String redisInstance;

    /**
     * 执行器组
     */
    private String group;

    /**
     * 处理的最大任务数
     */
    private Integer maxTaskCount;

    /**
     * 轮询频率(ms) 默认200ms
     */
    private Long pollingFrequencyMillis;

    /**
     * 同步任务状态频率(ms) 默认1分钟
     */
    private Long syncStateFrequencyMillis;

    /**
     * 任务抢占锁存活时间
     */
    private Integer taskLockExpireSeconds;

    /**
     * 节点丢失阈值
     */
    private Integer lostThreshold;

    /**
     * 数据存储的桶名称
     */
    private String dataBucketName;

    /**
     * 任务数据在桶中的目录
     */
    private String dataDirectory;

    /**
     * 重试告警阈值，默认重试三次后就触发告警
     */
    private Integer warnThreshold;

    public ExecutorProperties() {
        enable = false;
        maxTaskCount = 1;
        pollingFrequencyMillis = 200L;
        syncStateFrequencyMillis = 60L * 1000;
        taskLockExpireSeconds = 60;
        lostThreshold = 4;
        warnThreshold = 3;
    }
}
