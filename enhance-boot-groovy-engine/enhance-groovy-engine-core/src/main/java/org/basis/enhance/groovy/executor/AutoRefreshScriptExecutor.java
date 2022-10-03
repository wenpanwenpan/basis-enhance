package org.basis.enhance.groovy.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.basis.enhance.groovy.config.properties.GroovyEngineProperties;
import org.basis.enhance.groovy.helper.RefreshScriptHelper;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * 自动定时刷新脚本执行器
 * </p>
 *
 * @author wenpan 2022/10/03 11:35
 */
@Slf4j
public class AutoRefreshScriptExecutor implements SmartLifecycle {

    private final GroovyEngineProperties groovyEngineProperties;
    private final RefreshScriptHelper refreshScriptHelper;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public AutoRefreshScriptExecutor(GroovyEngineProperties groovyEngineProperties,
                                     RefreshScriptHelper refreshScriptHelper) {
        this.groovyEngineProperties = groovyEngineProperties;
        this.refreshScriptHelper = refreshScriptHelper;
    }

    @Override
    public void start() {
        // bean初始化完毕后会被吊起，是否吊起需要看isAutoStartup返回值，如果返回false则start不会被吊起
        if (!isRunning.compareAndSet(false, true)) {
            log.error("Note AutoRefreshScriptExecutor already started, skip.");
            return;
        }
        log.info("AutoRefreshScriptExecutor thread start.");
        // 启动刷新线程
        ScheduledExecutorService register =
                new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
                        .namingPattern("enhance-groovy-engine-executor")
                        .daemon(true)
                        .build());
        // 定时任务线程池定时刷新
        register.scheduleAtFixedRate(this::refreshScript,
                groovyEngineProperties.getInitialDelay(),
                groovyEngineProperties.getPollingCycle(),
                TimeUnit.SECONDS);
        log.info("AutoRefreshScriptExecutor thread complete.");
    }

    @Override
    public void stop() {
        // 容器关闭后，spring容器发现当前对象实现了SmartLifecycle，就调用stop(Runnable)，如果只是实现了Lifecycle，就调用stop()
        log.warn("container is stopping, stop auto refresh script now.");
        isRunning.compareAndSet(true, false);
    }

    @Override
    public boolean isRunning() {
        // 组件是否在运行中
        return isRunning.get();
    }

    @Override
    public boolean isAutoStartup() {
        // 当容器没有启动过时才吊起start
        return !isRunning.get();
    }

    @Override
    public int getPhase() {
        // 返回值决定start方法在众多Lifecycle实现类中的执行顺序(stop也是)
        return 0;
    }

    /**
     * 刷新脚本
     */
    private void refreshScript() {
        if (isRunning.get()) {
            refreshScriptHelper.refreshAll();
        } else {
            log.warn("can not refresh script because isRunning status is false.");
        }
    }
}
