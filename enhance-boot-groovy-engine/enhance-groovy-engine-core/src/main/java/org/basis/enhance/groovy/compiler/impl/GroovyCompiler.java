package org.basis.enhance.groovy.compiler.impl;

import groovy.lang.GroovyClassLoader;
import org.basis.enhance.groovy.compiler.DynamicCodeCompiler;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * groovy编译器
 *
 * @author wenpan 2022/9/18 11:40 上午
 */
public class GroovyCompiler implements DynamicCodeCompiler {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyCompiler.class);

    @Override
    public Class<?> compile(String code, String name) {
        GroovyClassLoader loader = getGroovyClassLoader();
        LOG.warn("Compiling filter: " + name);
        return (Class<?>) loader.parseClass(code, name);
    }

    @Override
    public Class<?> compile(ScriptEntry scriptEntry) {
        GroovyClassLoader loader = getGroovyClassLoader();
        // 以脚本的hashCode作为类名称 todo 待考虑
        return loader.parseClass(scriptEntry.getScriptContext(),
                GroovyCompiler.class.getSimpleName() + scriptEntry.getScriptContext().hashCode());
    }

    /**
     * @return a new GroovyClassLoader
     */
    GroovyClassLoader getGroovyClassLoader() {
        // todo 这里每次都新创建一个GroovyClassLoader合适吗？
        return new GroovyClassLoader();
    }

}