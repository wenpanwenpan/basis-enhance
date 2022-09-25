package org.basis.enhance.groovy.executor.impl;

import groovy.lang.Binding;
import groovy.lang.Script;
import org.basis.enhance.groovy.constants.ExecutionStatus;
import org.basis.enhance.groovy.constants.GroovyEngineConstants;
import org.basis.enhance.groovy.entity.EngineExecutorResult;
import org.basis.enhance.groovy.entity.ExecuteParams;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.executor.EngineExecutor;
import org.basis.enhance.groovy.helper.ApplicationContextHelper;
import org.basis.enhance.groovy.registry.ScriptRegistry;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.util.Objects;

/**
 * 默认引擎执行器
 *
 * @author wenpan 2022/09/18 12:39
 */
public class DefaultEngineExecutor implements EngineExecutor {

    private ScriptRegistry scriptRegistry;

    public DefaultEngineExecutor(ScriptRegistry scriptRegistry) {
        this.scriptRegistry = scriptRegistry;
    }

    @Override
    public EngineExecutorResult execute(ScriptQuery scriptQuery, ExecuteParams executeParams) throws Exception {
        // 先根据scriptEntryQuery查询到要执行的脚本
        ScriptEntry scriptEntry = scriptRegistry.find(scriptQuery);
        return execute(scriptEntry, executeParams);
    }

    @Override
    public EngineExecutorResult execute(ScriptEntry scriptEntry, ExecuteParams executeParams) {
        // 没有脚本，则直接返回NO_SCRIPT，响应content为null
        if (Objects.isNull(scriptEntry)) {
            return EngineExecutorResult.success(ExecutionStatus.NO_SCRIPT, null);
        }
        // 构建binding入参
        Binding binding = buildBinding(executeParams);

        // 创建脚本（可以看到这里就是基于Class去new一个script对象）
        Script script = InvokerHelper.createScript(scriptEntry.getClazz(), binding);
        Object result;
        try {
            // 执行脚本
            result = script.run();
        } catch (Exception ex) {
            return EngineExecutorResult.failed(ex);
        }

        // 返回执行结果
        return EngineExecutorResult.success(result);
    }

    /**
     * 构建binding信息
     */
    private Binding buildBinding(ExecuteParams params) {
        Binding binding = new Binding();
        // 没有需要传递的参数
        if (Objects.isNull(params)) {
            return binding;
        }
        // 将spring容器上下文放入脚本
        binding.setProperty(GroovyEngineConstants.ContextConstants.APPLICATION_CONTEXT, ApplicationContextHelper.getContext());
        params.forEach(binding::setProperty);
        return binding;
    }

}
