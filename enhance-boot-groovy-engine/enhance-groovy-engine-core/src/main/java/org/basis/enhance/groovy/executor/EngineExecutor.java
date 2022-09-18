package org.basis.enhance.groovy.executor;

import org.basis.enhance.groovy.entity.EngineExecutorResult;
import org.basis.enhance.groovy.entity.ExecuteParams;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;

/**
 * 引擎执行器
 *
 * @author wenpan 2022/09/18 12:25
 */
public interface EngineExecutor {

    /**
     * 执行脚本
     *
     * @param scriptQuery   执行条件
     * @param executeParams 业务参数（传递到groovy脚本里的参数都可以放这里面）
     * @return org.basis.enhance.groovy.entity.EngineExecutorResult
     * @throws Exception 异常
     * @author wenpan 2022/9/18 12:57 下午
     */
    EngineExecutorResult execute(ScriptQuery scriptQuery, ExecuteParams executeParams) throws Exception;

    /**
     * 执行脚本
     *
     * @param scriptEntry   脚本实体
     * @param executeParams 业务参数（传递到groovy脚本里的参数都可以放这里面）
     * @return org.basis.enhance.groovy.entity.EngineExecutorResult
     * @throws Exception 异常
     * @author wenpan 2022/9/18 12:57 下午
     */
    EngineExecutorResult execute(ScriptEntry scriptEntry, ExecuteParams executeParams) throws Exception;

}
