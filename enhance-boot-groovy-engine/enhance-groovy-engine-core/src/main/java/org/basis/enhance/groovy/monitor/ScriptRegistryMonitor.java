package org.basis.enhance.groovy.monitor;

import org.basis.enhance.groovy.entity.ScriptEntry;

import java.util.Map;

/**
 * 脚本注册监控
 *
 * @author wenpanfeng 2022/09/30 15:14
 */
public class ScriptRegistryMonitor implements Monitor<Map<String, ScriptEntry>> {

    @Override
    public Map<String, ScriptEntry> fetchWholeData() {
        
        return null;
    }
}