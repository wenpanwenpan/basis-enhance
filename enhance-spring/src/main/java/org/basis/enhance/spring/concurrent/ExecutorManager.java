package org.basis.enhance.spring.concurrent;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池管理器
 *
 * @author Mr_wenpan@163.com 2021/10/09 16:34
 */
public class ExecutorManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorManager.class);

    /**
     * 线程池缓存
     */
    private static final ConcurrentHashMap<String, ThreadPoolExecutor> EXECUTORS = new ConcurrentHashMap<>(8);

    /**
     * 向管理器注册线程池
     *
     * @param threadPoolName 线程池名称
     * @param executor       ThreadPoolExecutor
     */
    public static void registerThreadPoolExecutor(String threadPoolName, ThreadPoolExecutor executor) {
        EXECUTORS.put(threadPoolName, executor);
    }

    /**
     * 向管理器注册线程池，并监控线程池状态
     *
     * @param threadPoolName 线程池名称
     * @param executor       ThreadPoolExecutor
     */
    public static void registerAndMonitorThreadPoolExecutor(String threadPoolName, ThreadPoolExecutor executor) {
        EXECUTORS.put(threadPoolName, executor);
        displayThreadPoolStatus(executor, threadPoolName);
        hookShutdownThreadPool(executor, threadPoolName);
    }

    /**
     * 根据名称获取线程池
     *
     * @param threadPoolName 线程池名称
     */
    public static ThreadPoolExecutor getThreadPoolExecutor(String threadPoolName) {
        return EXECUTORS.get(threadPoolName);
    }

    /**
     * 获取所有已注册的线程池
     *
     * @return ThreadPoolExecutor
     */
    public static Map<String, ThreadPoolExecutor> getAllThreadPoolExecutor() {
        return ImmutableMap.copyOf(EXECUTORS);
    }

    /**
     * 根据名称移除已注册的线程池
     *
     * @param threadPoolName 线程池名称
     */
    public static void removeThreadPoolExecutor(String threadPoolName) {
        EXECUTORS.remove(threadPoolName);
    }

    /**
     * 添加Hook在Jvm关闭时优雅的关闭线程池（JVM会等待钩子执行完毕后再全部关闭，所以钩子执行时间不能过长）
     *
     * @param threadPool     线程池
     * @param threadPoolName 线程池名称
     */
    public static void hookShutdownThreadPool(ExecutorService threadPool, String threadPoolName) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("[>>ExecutorShutdown<<] Start to shutdown the thead pool: [{}]", threadPoolName);
            // 使新任务无法提交（但正在运行中的任务仍然在运行）
            threadPool.shutdown();
            try {
                // 等待未完成任务结束（如果60秒后还有任务仍然没有结束，那么直接调用shutdownNow去取消正在运行的任务）
                // 注意：这里如果正在运行的任务不响应中断那么这里无法取消
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    // 取消当前执行的任务
                    threadPool.shutdownNow();
                    LOGGER.warn("[>>ExecutorShutdown<<] Interrupt the worker, which may cause some task inconsistent. Please check the biz logs.");

                    // 等待任务取消的响应（如果任务不响应中断，那么取消失败，则在这里输出提示日志）
                    if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                        LOGGER.error("[>>ExecutorShutdown<<] Thread pool can't be shutdown even with interrupting worker threads, which may cause some task inconsistent. Please check the biz logs.");
                    }
                }
            } catch (InterruptedException ie) {
                // 重新取消当前线程进行中断
                threadPool.shutdownNow();
                LOGGER.error("[>>ExecutorShutdown<<] The current server thread is interrupted when it is trying to stop the worker threads. This may leave an inconsistent state. Please check the biz logs.");
                // 保留中断状态
                Thread.currentThread().interrupt();
            }

            LOGGER.info("[>>ExecutorShutdown<<] Finally shutdown the thead pool: [{}]", threadPoolName);
        }));
    }

    /**
     * 根据一定周期输出线程池的状态
     *
     * @param threadPool     线程池
     * @param threadPoolName 线程池名称
     */
    public static void displayThreadPoolStatus(ThreadPoolExecutor threadPool, String threadPoolName) {
        // 60 ~ 600s随机产生一个时间输出线程池状态
        displayThreadPoolStatus(threadPool, threadPoolName, RandomUtils.nextInt(60, 600), TimeUnit.SECONDS);
    }

    /**
     * 根据一定周期输出线程池的状态
     *
     * @param threadPool     线程池
     * @param threadPoolName 线程池名称
     * @param period         周期
     * @param unit           时间单位
     */
    public static void displayThreadPoolStatus(ThreadPoolExecutor threadPool, String threadPoolName, long period, TimeUnit unit) {
        // 新建一个线程的线程池，用于定时输出线程池的状态
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            final String payload = "[>>ExecutorStatus<<] ThreadPool Name: [{}], Pool Status: [shutdown={}, Terminated={}], Pool Thread Size: {}, Largest Pool Size: {}, Active Thread Count: {}, Task Count: {}, Tasks Completed: {}, Tasks in Queue: {}";
            Object[] params = new Object[]{
                    threadPoolName,
                    // 是否已经Shutdown
                    threadPool.isShutdown(),
                    // 线程是否被终止
                    threadPool.isTerminated(),
                    // 线程池线程数量
                    threadPool.getPoolSize(),
                    // 线程最大达到的数量
                    threadPool.getLargestPoolSize(),
                    // 工作线程数
                    threadPool.getActiveCount(),
                    // 总任务数
                    threadPool.getTaskCount(),
                    // 已完成的任务数
                    threadPool.getCompletedTaskCount(),
                    // 队列中的现有任务数
                    threadPool.getQueue().size()};

            if (threadPool.getQueue().remainingCapacity() < 64) {
                LOGGER.warn(payload, params);
            } else {
                LOGGER.info(payload, params);
            }
        }, 0, period, unit);
    }

}
