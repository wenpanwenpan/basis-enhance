package org.basis.enhance.groovy.compiler.impl;

import groovy.lang.GroovyClassLoader;
import org.basis.enhance.groovy.compiler.DynamicCodeCompiler;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * groovy编译器
 * </p>
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
        // 以 GroovyCompiler + 脚本的名称作为类名称
        return loader.parseClass(scriptEntry.getScriptContext(),
                GroovyCompiler.class.getSimpleName() + scriptEntry.getName());
    }

    /**
     * <p>
     * 为什么要New 一个class loader呢？这个就要从Class对象垃圾回收说起，一个Class要被回收必须满足以下条件：
     *  <ol>
     *     <li>该Class 的所有实例都已经被回收</li>
     *     <li>加载该类的classLoader已经被回收</li>
     *     <li>该Class 没有被引用</li>
     *  </ol>
     * 通过使用 new 一个classLoader 来加载动态脚本就是为了解决动态类回收问题，因为classLoader可以提前被回收
     * </p>
     *
     * @return a new GroovyClassLoader
     */
    public GroovyClassLoader getGroovyClassLoader() {

        return new GroovyClassLoader();
    }

}