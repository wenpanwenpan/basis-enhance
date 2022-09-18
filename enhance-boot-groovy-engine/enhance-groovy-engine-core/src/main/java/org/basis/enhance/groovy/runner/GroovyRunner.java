package org.basis.enhance.groovy.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.basis.enhance.groovy.config.properties.GroovyEngineProperties;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.basis.enhance.groovy.registry.ScriptRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * groovy runner
 *
 * @author wenpan 2022/9/18 5:06 下午
 */
@Slf4j
@Component
public class GroovyRunner implements CommandLineRunner {

    @Autowired
    private ScriptLoader scriptLoader;

    @Autowired
    private ScriptRegistry scriptRegistry;

    @Autowired
    private GroovyEngineProperties groovyEngineProperties;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public void run(String... args) {
        if (!isRunning.compareAndSet(false, true)) {
            log.error("Note GroovyRunner already started, skip.");
            return;
        }
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
    }

    private void refresh() {
        try {
            // 加载所有脚本
            List<ScriptEntry> scriptEntries = scriptLoader.load();
            for (ScriptEntry scriptEntry : scriptEntries) {
                ScriptEntry oldScriptEntry = scriptRegistry.find(new ScriptQuery(scriptEntry.getName()));
                // 缓存中没有，则注册进缓存
                if (Objects.isNull(oldScriptEntry)) {
                    scriptRegistry.register(scriptEntry);
                    continue;
                }
                boolean isEquals = oldScriptEntry.fingerprintIsEquals(scriptEntry.getFingerprint());
                // 指纹相同，则说明脚本没有变化，指纹不同则说明脚本有变化
                if (!isEquals) {
                    scriptRegistry.register(scriptEntry);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("scriptLoader.load() occur error.", ex);
        }
    }
} 