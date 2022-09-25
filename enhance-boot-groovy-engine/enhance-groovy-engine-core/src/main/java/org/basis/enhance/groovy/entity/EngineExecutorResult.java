package org.basis.enhance.groovy.entity;

import lombok.Data;
import org.basis.enhance.groovy.constants.ExecutionStatus;

/**
 * 脚本执行结果
 *
 * @author wenpan 2022/09/18 12:44
 */
@Data
public class EngineExecutorResult {

    /**
     * 执行状态
     */
    private ExecutionStatus executionStatus;

    /**
     * 返回内容
     */
    private Object context;

    /**
     * 异常信息
     */
    private Throwable exception;

    private EngineExecutorResult(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    private EngineExecutorResult(ExecutionStatus executionStatus, Throwable exception) {
        this.executionStatus = executionStatus;
        this.exception = exception;
    }

    private EngineExecutorResult(ExecutionStatus executionStatus, Object context) {
        this.executionStatus = executionStatus;
        this.context = context;
    }

    /**
     * 执行失败
     *
     * @param exception 异常信息
     * @return org.basis.enhance.groovy.entity.EngineExecutorResult<java.lang.Object>
     * @author wenpan 2022/9/18 12:54 下午
     */
    public static EngineExecutorResult failed(Throwable exception) {
        return new EngineExecutorResult(ExecutionStatus.FAILED, exception);
    }

    /**
     * 执行成功
     *
     * @param context 内容
     * @return org.basis.enhance.groovy.entity.EngineExecutorResult<java.lang.Object>
     * @author wenpan 2022/9/18 12:55 下午
     */
    public static EngineExecutorResult success(Object context) {
        return success(ExecutionStatus.SUCCESS, context);
    }

    /**
     * 执行成功
     *
     * @param context 内容
     * @param status  执行状态
     * @return org.basis.enhance.groovy.entity.EngineExecutorResult<java.lang.Object>
     * @author wenpan 2022/9/18 12:55 下午
     */
    public static EngineExecutorResult success(ExecutionStatus status, Object context) {
        return new EngineExecutorResult(status, context);
    }
}
