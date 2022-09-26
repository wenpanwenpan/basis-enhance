package org.basis.enhance.groovy.registry.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.enhance.groovy.registry.ScriptRegistry;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认注册中心
 *
 * @author wenpan 2022/09/18 11:49
 */
@Slf4j
public class DefaultScriptRegistry implements ScriptRegistry {

    /**
     * 使用饿汉单例保证线程安全
     */
    private final static Map<String, ScriptEntry> SCRIPT_ENGINE_ENTRY_CACHE = new ConcurrentHashMap<>();

    @Setter
    @Getter
    private ScriptLoader scriptLoader;

    public DefaultScriptRegistry(ScriptLoader scriptLoader) {
        this.scriptLoader = scriptLoader;
    }

    @Override
    public Boolean register(ScriptEntry scriptEntry) {
        if (Objects.isNull(scriptEntry)) {
            return true;
        }
        try {
            // 强制覆盖
            SCRIPT_ENGINE_ENTRY_CACHE.put(scriptEntry.getName(), scriptEntry);
        } catch (Exception ex) {
            log.error("DefaultScriptRegistry#register occur exception.", ex);
            return false;
        }
        return true;
    }

    @Override
    public Boolean register(ScriptEntry scriptEntry, boolean allowToCover) {
        ScriptEntry entry = SCRIPT_ENGINE_ENTRY_CACHE.get(scriptEntry.getName());
        if (Objects.isNull(entry) || allowToCover) {
            return register(scriptEntry);
        }
        log.error("can not register [{}], because [{}] already exists, please check.",
                scriptEntry.getName(), scriptEntry.getLastModifiedTime());
        // 不覆盖，返回false
        return false;
    }

    @Override
    public ScriptEntry find(ScriptQuery scriptQuery) throws Exception {
        // 先从缓存中查找
        ScriptEntry entry = SCRIPT_ENGINE_ENTRY_CACHE.get(scriptQuery.getUniqueKey());

        if (Objects.nonNull(entry)) {
            return entry;
        }

        // todo 如果恶意操作每次都加载不存在的脚本，这里上锁有效率问题
        // 缓存中没有则通过脚本加载器进行加载
        synchronized (scriptQuery.getUniqueKey()) {
            entry = SCRIPT_ENGINE_ENTRY_CACHE.get(scriptQuery.getUniqueKey());
            // DCL
            if (Objects.isNull(entry)) {
                // 加载脚本
                entry = scriptLoader.load(scriptQuery);
                // 没有加载到脚本
                if (Objects.isNull(entry)) {
                    log.error("can not found ScriptEntry by scriptQuery : {}", scriptQuery);
                    return null;
                }
                // 设置脚本名称
                entry.setName(scriptQuery.getUniqueKey());
                // 放入缓存
                if (!register(entry)) {
                    log.error("put ScriptEntry to cache failed, name is [{}], lastModifiedTime is [{}], " +
                            "scriptContext is [{}]", entry.getName(), entry.getLastModifiedTime(), entry.getScriptContext());
                }
            }
        }

        return entry;
    }

    @Override
    public void clear() {
        SCRIPT_ENGINE_ENTRY_CACHE.clear();
    }

    @Override
    public Boolean clear(ScriptQuery scriptQuery) {
        SCRIPT_ENGINE_ENTRY_CACHE.remove(scriptQuery.getUniqueKey());
        return true;
    }

}
