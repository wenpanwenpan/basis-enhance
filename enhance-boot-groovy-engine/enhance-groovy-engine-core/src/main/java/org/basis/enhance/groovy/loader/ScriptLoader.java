package org.basis.enhance.groovy.loader;

import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;

import java.util.List;

/**
 * 脚本加载器
 *
 * @author wenpan 2022/09/18 11:32
 */
public interface ScriptLoader {

    /**
     * 加载脚本
     *
     * @param query 查询对象
     * @return org.basis.enhance.groovy.entity.ScriptEntry 脚本实体
     * @throws Exception 异常
     * @author wenpan 2022/9/18 12:13 下午
     */
    ScriptEntry load(ScriptQuery query) throws Exception;

    /**
     * 预加载所有的脚本
     *
     * @return java.util.List<org.basis.enhance.groovy.entity.ScriptEntry>
     * @throws Exception 异常
     * @author wenpan 2022/9/18 3:57 下午
     */
    List<ScriptEntry> load() throws Exception;

}
