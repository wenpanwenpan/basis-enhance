package org.basis.enhance.groovy.alarm;

import org.basis.enhance.groovy.entity.ScriptEntry;

import java.util.List;

/**
 * 热加载脚本告警接口
 *
 * @author wenpan 2022/09/29 22:45
 */
public interface HotLoadingGroovyScriptAlarm {

    /**
     * 加载脚本异常告警
     *
     * @param scriptEntries 脚本实集合
     * @param throwable     异常信息
     * @author wenpan 2022/9/29 10:46 下午
     */
    void alarm(List<ScriptEntry> scriptEntries, Throwable throwable);

}
