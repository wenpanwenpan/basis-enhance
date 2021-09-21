package org.basis.enhance.executor.handler;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.executor.config.property.ExecutorProperties;
import org.basis.enhance.executor.domain.entity.Task;
import org.springframework.core.annotation.Order;

/**
 * 默认重试告警器实现
 *
 * @author Mr_wenpan@163.com 2021/08/18 18:00
 */
@Slf4j
@Order(0)
public class DefaultRetryWarnHandler implements RetryWarnHandler {

    ExecutorProperties executorProperties;

    public DefaultRetryWarnHandler(ExecutorProperties executorProperties) {
        this.executorProperties = executorProperties;
    }

    @Override
    public void doRetryWarn(Task task) {
        // do nothing .....
        log.warn("分布式任务重试超过重试阈值 [{}], 请关注. 任务信息：{}", executorProperties.getWarnThreshold(), task);
    }

}
