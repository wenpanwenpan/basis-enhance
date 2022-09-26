package org.basis.enhance.groovy.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.basis.enhance.groovy.compiler.DynamicCodeCompiler;
import org.basis.enhance.groovy.config.properties.GroovyEngineProperties;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.exception.LoadScriptException;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.enhance.groovy.registry.ScriptRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * 热加载 groovy 脚本 runner
 * </p>
 *
 * @author wenpan 2022/9/18 5:06 下午
 */
@Slf4j
public class HotLoadingGroovyScriptRunner implements CommandLineRunner {

    @Autowired
    private ScriptLoader scriptLoader;

    @Autowired
    private ScriptRegistry scriptRegistry;

    @Autowired
    private GroovyEngineProperties groovyEngineProperties;

    @Autowired
    private DynamicCodeCompiler dynamicCodeCompiler;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public void run(String... args) {
        if (!isRunning.compareAndSet(false, true)) {
            log.error("Note GroovyRunner already started, skip.");
            return;
        }
        log.info("GroovyRunner thread start.");
        // 启动刷新线程
        ScheduledExecutorService register =
                new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
                        .namingPattern("enhance-groovy-engine-runner")
                        .daemon(true)
                        .build());
        // 定时任务线程池定时刷新
        register.scheduleAtFixedRate(this::refresh,
                groovyEngineProperties.getInitialDelay(),
                groovyEngineProperties.getPollingCycle(),
                TimeUnit.SECONDS);
        log.info("GroovyRunner thread complete.");
    }

    private void refresh() {
        log.info("scheduled Task Thread start refresh groovy script.");
        // 记录新增的脚本数量
        int addScriptCount = 0;
        int updateScriptCount = 0;
        List<ScriptEntry> scriptEntries;
        try {
            // 加载所有脚本
            scriptEntries = scriptLoader.load();
            log.debug("scheduled Task Thread load script is : {}", scriptEntries);
            log.info("scheduled Task Thread load script count is : {}", scriptEntries.size());
            for (ScriptEntry scriptEntry : scriptEntries) {
                ScriptEntry oldScriptEntry = scriptRegistry.find(new ScriptQuery(scriptEntry.getName()));
                // 缓存中没有，则注册进缓存
                if (Objects.isNull(oldScriptEntry)) {
                    log.info("can not found script by [{}], register to registry directly.", scriptEntry.getName());
                    scriptRegistry.register(scriptEntry);
                    addScriptCount++;
                    continue;
                }
                boolean isEquals = oldScriptEntry.fingerprintIsEquals(scriptEntry.getFingerprint());
                // 指纹相同，则说明脚本没有变化，指纹不同则说明脚本有变化
                if (!isEquals) {
                    log.info("found script by [{}], but their fingerprints are different, modify it.", scriptEntry.getName());
                    // 编译脚本为Class
                    Class<?> aClass = dynamicCodeCompiler.compile(scriptEntry);
                    scriptEntry.setClazz(aClass);
                    scriptEntry.setLastModifiedTime(System.currentTimeMillis());
                    // 注册
                    scriptRegistry.register(scriptEntry);
                    updateScriptCount++;
                    log.info("modify [{}] complete.", scriptEntry.getName());
                }
            }
        } catch (Exception ex) {
            throw new LoadScriptException("scriptLoader.load() occur error.", ex);
        }
        // 打印刷新情况
        log.info("scheduled Task Thread refresh groovy script end,scriptCount is [{}], addScriptCount is [{}]," +
                " updateScriptCount is [{}]", scriptEntries.size(), addScriptCount, updateScriptCount);
    }
} 