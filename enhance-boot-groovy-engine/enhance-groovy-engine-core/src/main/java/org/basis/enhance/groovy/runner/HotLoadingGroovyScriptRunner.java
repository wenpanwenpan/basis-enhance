package org.basis.enhance.groovy.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.basis.enhance.groovy.config.properties.GroovyEngineProperties;
import org.basis.enhance.groovy.helper.RefreshScriptHelper;
import org.springframework.boot.CommandLineRunner;

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

    private final GroovyEngineProperties groovyEngineProperties;
    private final RefreshScriptHelper refreshScriptHelper;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public HotLoadingGroovyScriptRunner(GroovyEngineProperties groovyEngineProperties,
                                        RefreshScriptHelper refreshScriptHelper) {
        this.groovyEngineProperties = groovyEngineProperties;
        this.refreshScriptHelper = refreshScriptHelper;
    }

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
        register.scheduleAtFixedRate(refreshScriptHelper::refreshAll,
                groovyEngineProperties.getInitialDelay(),
                groovyEngineProperties.getPollingCycle(),
                TimeUnit.SECONDS);
        log.info("GroovyRunner thread complete.");
    }

} 