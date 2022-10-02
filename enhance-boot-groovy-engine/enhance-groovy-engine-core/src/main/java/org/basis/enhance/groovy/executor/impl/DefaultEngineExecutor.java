package org.basis.enhance.groovy.executor.impl;

import groovy.lang.Binding;
import groovy.lang.Script;
import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.groovy.constants.GroovyEngineConstants;
import org.basis.enhance.groovy.entity.EngineExecutorResult;
import org.basis.enhance.groovy.entity.ExecuteParams;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.exception.LoadScriptException;
import org.basis.enhance.groovy.executor.EngineExecutor;
import org.basis.enhance.groovy.helper.ApplicationContextHelper;
import org.basis.enhance.groovy.registry.ScriptRegistry;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * 默认引擎执行器
 *
 * @author wenpan 2022/09/18 12:39
 */
public class DefaultEngineExecutor implements EngineExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ScriptRegistry scriptRegistry;

    public DefaultEngineExecutor(ScriptRegistry scriptRegistry) {
        this.scriptRegistry = scriptRegistry;
    }

    @NonNull
    @Override
    public EngineExecutorResult execute(@NonNull ScriptQuery scriptQuery, ExecuteParams executeParams) {
        // 先根据scriptEntryQuery查询到要执行的脚本
        ScriptEntry scriptEntry;
        try {
            scriptEntry = scriptRegistry.find(scriptQuery);
            // 没有找到脚本，抛出异常
            if (Objects.isNull(scriptEntry)) {
                throw new LoadScriptException(String.format("can not found script by [%s]", scriptQuery.getUniqueKey()));
            }
        } catch (Exception ex) {
            logger.error("execute groovy script error, scriptQuery is : {}, " +
                    "executeParams is : {}", scriptQuery, executeParams, ex);
            return EngineExecutorResult.failed(ex);
        }
        return execute(scriptEntry, executeParams);
    }

    @NonNull
    @Override
    public EngineExecutorResult execute(@NonNull ScriptEntry scriptEntry, ExecuteParams executeParams) {

        logger.debug("DefaultEngineExecutor start execute script, scriptEntry is : {}, " +
                "executeParams is : {}", scriptEntry, executeParams);

        Object result;
        try {
            // 构建binding入参
            Binding binding = buildBinding(executeParams);
            // 创建脚本（可以看到这里就是基于Class去new一个script对象）
            Script script = InvokerHelper.createScript(scriptEntry.getClazz(), binding);
            script.setBinding(binding);
            // 执行脚本
            result = script.run();
        } catch (Exception ex) {
            logger.error("execute groovy script error, scriptEntry is : {}," +
                    " executeParams is : {}", scriptEntry, executeParams, ex);
            return EngineExecutorResult.failed(ex);
        }

        logger.debug("DefaultEngineExecutor execute script success, result is : {}", result);

        // 返回执行结果
        return EngineExecutorResult.success(result);
    }

    @NonNull
    @Override
    public EngineExecutorResult execute(@NonNull String groovyMethodName,
                                        @NonNull ScriptQuery scriptQuery,
                                        ExecuteParams executeParams) {
        // 先根据scriptEntryQuery查询到要执行的脚本
        ScriptEntry scriptEntry;
        try {
            scriptEntry = scriptRegistry.find(scriptQuery);
            // 没有找到脚本，抛出异常
            if (Objects.isNull(scriptEntry)) {
                throw new LoadScriptException(String.format("can not found script by [%s]", scriptQuery.getUniqueKey()));
            }
        } catch (Exception ex) {
            logger.error("execute groovy script by groovyMethodName error, scriptQuery is : {}, " +
                    "executeParams is : {}", scriptQuery, executeParams, ex);
            return EngineExecutorResult.failed(ex);
        }
        return execute(groovyMethodName, scriptEntry, executeParams);
    }

    @NonNull
    @Override
    public EngineExecutorResult execute(@NonNull String groovyMethodName,
                                        @NonNull ScriptEntry scriptEntry,
                                        @NonNull ExecuteParams executeParams) {

        logger.debug("DefaultEngineExecutor start execute script by groovyMethodName, scriptEntry is : {}, " +
                "executeParams is : {}", scriptEntry, executeParams);

        if (StringUtils.isBlank(groovyMethodName)) {
            return EngineExecutorResult.failed("groovyMethodName can not be null.");
        }

        Object result;
        try {
            // 构建binding入参
            Binding binding = buildBinding(executeParams);
            // 创建脚本（可以看到这里就是基于Class去new一个script对象）
            Script script = InvokerHelper.createScript(scriptEntry.getClazz(), binding);
            // 按照groovy里的方法名来执行脚本
            result = script.invokeMethod(groovyMethodName, executeParams);
        } catch (Exception ex) {
            logger.error("execute groovy script  by groovyMethodName error, scriptEntry is : {}," +
                    " executeParams is : {}", scriptEntry, executeParams, ex);
            return EngineExecutorResult.failed(ex);
        }

        logger.debug("DefaultEngineExecutor execute script by groovyMethodName success, result is : {}", result);

        // 返回执行结果
        return EngineExecutorResult.success(result);
    }

    /**
     * 构建binding信息
     */
    private Binding buildBinding(ExecuteParams params) {
        Binding binding = new Binding();
        // 将spring容器上下文放入脚本
        binding.setProperty(GroovyEngineConstants.ContextConstants.APPLICATION_CONTEXT, ApplicationContextHelper.getContext());
        // 没有需要传递的参数
        if (Objects.isNull(params)) {
            return binding;
        }
        params.forEach(binding::setProperty);
        return binding;
    }

}
