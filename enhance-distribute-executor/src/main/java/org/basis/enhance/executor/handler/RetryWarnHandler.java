package org.basis.enhance.executor.handler;


import org.basis.enhance.executor.domain.entity.Task;

/**
 * 重试告警处理器
 *
 * @author Mr_wenpan@163.com 2021/08/18 17:44
 */
public interface RetryWarnHandler {

    /**
     * 处理重试超过阈值告警
     *
     * @param task 任务信息
     */
    void doRetryWarn(Task task);

}
