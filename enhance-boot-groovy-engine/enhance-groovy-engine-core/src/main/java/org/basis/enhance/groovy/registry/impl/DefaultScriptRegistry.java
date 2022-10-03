package org.basis.enhance.groovy.registry.impl;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.exception.LoadScriptException;
import org.basis.enhance.groovy.exception.RegisterScriptException;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.enhance.groovy.registry.ScriptRegistry;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 默认注册中心
 *
 * @author wenpan 2022/09/18 11:49
 */
@Slf4j
public class DefaultScriptRegistry implements ScriptRegistry {

    /**
     * 咖啡因缓存
     */
    @Getter
    private final Cache<String, ScriptEntry> cache;

    @Setter
    @Getter
    private ScriptLoader scriptLoader;

    public DefaultScriptRegistry(ScriptLoader scriptLoader, Cache<String, ScriptEntry> cache) {
        this.scriptLoader = scriptLoader;
        this.cache = cache;
    }

    @Override
    public Boolean register(ScriptEntry scriptEntry) {
        if (Objects.isNull(scriptEntry)) {
            log.warn("register scriptEntry failed, because it is null.");
            return true;
        }
        try {
            // 强制覆盖
            cache.put(scriptEntry.getName(), scriptEntry);
        } catch (Exception ex) {
            log.error("DefaultScriptRegistry#register occur exception.", ex);
            return false;
        }
        return true;
    }

    @Override
    public Boolean batchRegister(List<ScriptEntry> scriptEntries) {
        log.debug("batch register start, scriptEntries is : {}", scriptEntries);
        Boolean success = batchRegister(scriptEntries, true);
        log.debug("batch register result is : [{}], scriptEntries is : {}", success, scriptEntries);
        return success;
    }

    @Override
    public Boolean register(ScriptEntry scriptEntry, boolean allowToCover) {
        // 旧entry
        ScriptEntry oldEntry = cache.getIfPresent(scriptEntry.getName());
        if (Objects.isNull(oldEntry) || allowToCover) {
            return register(scriptEntry);
        }
        log.error("can not register [{}], because [{}] already exists, please check.",
                scriptEntry.getName(), scriptEntry.getLastModifiedTime());
        // 不覆盖，返回false
        return false;
    }

    @Override
    public Boolean batchRegister(List<ScriptEntry> scriptEntries, boolean allowToCover) {
        log.debug("batch register start, scriptEntries is : {}, allowToCover is : {}", scriptEntries, allowToCover);
        if (CollectionUtils.isEmpty(scriptEntries)) {
            log.warn("scriptEntries is empty, not register.");
            return true;
        }
        boolean executeResult = true;
        for (ScriptEntry scriptEntry : scriptEntries) {
            executeResult &= register(scriptEntry, allowToCover);
        }
        log.debug("batch register success, scriptEntries is : {}, allowToCover is : {}", scriptEntries, allowToCover);
        return executeResult;
    }

    @Override
    public ScriptEntry findOnCache(ScriptQuery scriptQuery) {
        // 直接从缓存中通过条件查询
        return cache.getIfPresent(scriptQuery.getUniqueKey());
    }

    @Override
    public ScriptEntry find(ScriptQuery scriptQuery) throws Exception {
        // 先从缓存中查找
        ScriptEntry entry = cache.getIfPresent(scriptQuery.getUniqueKey());

        if (Objects.nonNull(entry)) {
            return entry;
        }

        // 缓存中没有则通过脚本加载器进行加载
        synchronized (scriptQuery.getUniqueKey()) {
            entry = cache.getIfPresent(scriptQuery.getUniqueKey());
            // DCL
            if (Objects.isNull(entry)) {
                log.info("DefaultScriptRegistry can not found ScriptEntry by scriptQuery [{}], load it now.", scriptQuery);
                // 加载脚本
                entry = scriptLoader.load(scriptQuery);
                // 没有加载到脚本
                if (Objects.isNull(entry)) {
                    log.error("can not found ScriptEntry by scriptQuery : {}", scriptQuery);
                    return null;
                }
                log.info("DefaultScriptRegistry ScriptEntry by scriptQuery [{}] success, ScriptEntry is {}.", scriptQuery, entry);
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
    public Map<String, ScriptEntry> findAllOnCache(boolean needLatestData) {
        // 需要数据，则先从数据源拉取，然后再返回
        if (needLatestData) {
            List<ScriptEntry> scriptEntries;
            try {
                log.info("findAllOnCache load script start.");
                scriptEntries = scriptLoader.load();
                log.info("findAllOnCache load script success, scriptEntries size is : [{}]",
                        Objects.isNull(scriptEntries) ? 0 : scriptEntries.size());
            } catch (Exception ex) {
                throw new LoadScriptException("load script by scriptLoader occur exception.", ex);
            }
            // 注册到本地注册中心
            if (!batchRegister(scriptEntries, false)) {
                // 注册失败，抛出异常，保证获取到的数据一定是最新的
                throw new RegisterScriptException("batch register failed.");
            }
        }
        return cache.asMap();
    }


    @Override
    public void clear() {
        log.warn("clear script registry start.");
        cache.invalidateAll();
        log.warn("clear script registry success.");
    }

    @Override
    public Boolean clear(ScriptQuery scriptQuery) {
        log.warn("start clear script registry by key: [{}].", scriptQuery.getUniqueKey());
        cache.invalidate(scriptQuery.getUniqueKey());
        log.warn("success clear script registry by key: [{}].", scriptQuery.getUniqueKey());
        return true;
    }

}
