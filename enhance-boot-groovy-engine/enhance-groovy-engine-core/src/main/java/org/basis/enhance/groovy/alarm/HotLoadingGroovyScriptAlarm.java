package org.basis.enhance.groovy.alarm;

import org.basis.enhance.groovy.entity.ScriptEntry;

/**
 * 热加载脚本告警接口
 *
 * @author wenpan 2022/09/29 22:45
 */
public interface HotLoadingGroovyScriptAlarm {

    /**
     * 加载脚本异常告警
     *
     * @param scriptEntry 脚本实体
     * @author wenpan 2022/9/29 10:46 下午
     */
    void alarm(ScriptEntry scriptEntry);

}
