package org.basis.enhance.executor.infra.server.monitor;

import lombok.Data;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.basis.enhance.executor.config.property.ExecutorProperties;
import org.basis.enhance.executor.infra.server.context.ExecutorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 任务监控
 *
 * @author Mr_wenpan@163.com 2021/8/18 9:31 下午
 */
@Data
public class TaskMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(TaskMonitor.class);

    /**
     * 关机标志位
     */
    private volatile boolean shutdownFlag;

    /**
     * 刷新频率
     */
    private Integer refreshFrequency;

    /**
     * 当前正在运行的任务ID集合
     */
    private Set<String> runningTasks;

    /**
     * 执行器上下文
     */
    private final ExecutorContext executorContext;

    private final ExecutorProperties properties;

    private final ReentrantLock lock = new ReentrantLock();

    public TaskMonitor(ExecutorContext executorContext) {
        shutdownFlag = false;
        this.executorContext = executorContext;
        properties = executorContext.getProperties();
        // =======================================================================================================
        // 采用同步set容器虽然可以保证线程安全，但是如果一个线程遍历容器中的元素一个线程修改容器中的元素就会出现问题（）
        // 所以这里有两种选择
        // 1、采用写时复制容器（该容器也有很大的缺点：内存开销比较高）
        // 2、采用读写都加锁，但效率很低
        // 综合项目的使用条件，几乎要10几秒才会读取一下该集合的值，但几乎每秒都会更新该集合的值，所以综合考虑这里采用加锁的方式
        // =======================================================================================================
        runningTasks = Collections.synchronizedSet(new HashSet<>());
        refreshFrequency = Math.max(properties.getTaskLockExpireSeconds() / properties.getLostThreshold(), 1);
    }

    public void addToMonitor(String taskKey) {
        if (lock.tryLock()) {
            try {
                runningTasks.add(taskKey);
            } finally {
                lock.unlock();
            }
        }
    }

    public void removeFromMonitor(String taskKey) {
        runningTasks.remove(taskKey);
    }

    public int runningTaskCount() {
        return runningTasks.size();
    }

    /**
     * 不断的延长任务的过期时间，类似看门狗
     */
    private void refresh() {
        String group = properties.getGroup();
        Integer taskLockExpireSeconds = properties.getTaskLockExpireSeconds();
        // 这里有并发修改异常，一个线程遍历一个线程修改。所以采用加锁解决
        if (lock.tryLock()) {
            try {
                for (String taskKey : runningTasks) {
                    try {
                        // 延长任务的过期时间，默认每隔15s去延长一下
                        executorContext.getTaskRedisRepository().expireTaskLock(group, taskKey, taskLockExpireSeconds);
                    } catch (Exception ex) {
                        LOG.error("refresh task[] lock error:{}", taskKey, ex);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void start() {
        // 守护线程随JVM关闭而停止
        ScheduledExecutorService register = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
                .namingPattern(THREAD_NAME)
                .daemon(true)
                .build());
        register.scheduleAtFixedRate(() -> {
            // 收到停机通知则退出监控
            while (!shutdownFlag) {
                try {
                    refresh();
                } catch (Exception ex) {
                    LOG.error("TaskMonitor refresh error:", ex);
                }
            }
        }, 0, refreshFrequency, TimeUnit.SECONDS);
    }

    public void shutdown() {
        shutdownFlag = true;
    }

    private static final String THREAD_NAME = "STONE_EXECUTOR_TASK_MONITOR";
}