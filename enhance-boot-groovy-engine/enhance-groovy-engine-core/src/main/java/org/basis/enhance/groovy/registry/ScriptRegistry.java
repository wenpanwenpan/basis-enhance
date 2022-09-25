package org.basis.enhance.groovy.registry;

import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;

/**
 * 引擎注册中心
 *
 * @author wenpan 2022/09/18 11:24
 */
public interface ScriptRegistry {

    /**
     * 注册脚本
     *
     * @param scriptEntry 脚本项
     * @return java.lang.Boolean
     * @author wenpan 2022/9/18 12:22 下午
     */
    Boolean register(ScriptEntry scriptEntry);

    /**
     * 注册脚本
     *
     * @param scriptEntry  脚本项
     * @param allowToCover 当有重复时是否允许覆盖
     * @return java.lang.Boolean
     * @author wenpan 2022/9/18 12:22 下午
     */
    Boolean register(ScriptEntry scriptEntry, boolean allowToCover);

    /**
     * 按条件查询脚本项
     *
     * @param scriptQuery 查询条件
     * @return org.basis.enhance.groovy.entity.ScriptEntry
     * @throws Exception 运行时异常
     * @author wenpan 2022/9/18 1:23 下午
     */
    ScriptEntry find(ScriptQuery scriptQuery) throws Exception;

    /**
     * 清除注册中心缓存
     *
     * @author wenpan 2022/9/18 5:34 下午
     */
    void clear();

    /**
     * 按条件清除
     *
     * @param scriptQuery 清除条件
     * @return java.lang.Boolean
     * @author wenpan 2022/9/18 5:35 下午
     */
    Boolean clear(ScriptQuery scriptQuery);


}
