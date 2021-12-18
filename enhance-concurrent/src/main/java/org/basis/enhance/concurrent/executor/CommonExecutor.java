package org.basis.enhance.concurrent.executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.basis.enhance.concurrent.exception.CommonException;
import org.basis.enhance.concurrent.infra.constant.EnhanceConcurrentConstants;
import org.basis.enhance.concurrent.queue.ThreadPriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 公用并发执行器（线程优先）
 * 适用于又想保证线程池里核心线程不销毁，最大线程数被销毁，但任务繁忙时优先开启更多的线程来执行任务的场景
 *
 * @author Mr_wenpan@163.com 2021/10/09 14:43
 */
public class CommonExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExecutor.class);

    /**
     * 共享线程池（线程优先线程池）
     */
    private static final ThreadPoolExecutor BASE_EXECUTOR;

    static {
        final String executorName = "BaseExecutor";
        BASE_EXECUTOR = buildThreadFirstExecutor(executorName);
    }

    /**
     * 构建线程优先的线程池(这里使用静态方法初始化，在该类被主动使用的时候触发类初始化进而为静态属性赋值)
     * <p>
     * 线程池默认是当核心线程数满了后，将任务添加到工作队列中，当工作队列满了之后，再创建线程直到达到最大线程数。
     *
     * <p>
     * 线程优先的线程池，就是在核心线程满了之后，继续创建线程，直到达到最大线程数之后，再把任务添加到工作队列中。
     *
     * <p>
     * 此方法默认设置核心线程数为 CPU 核数，最大线程数为 8倍 CPU 核数，空闲线程超过 5 分钟销毁，工作队列大小为 65536。
     *
     * @param poolName 线程池名称
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor buildThreadFirstExecutor(String poolName) {
        int coreSize = CommonExecutor.getCpuProcessors();
        int maxSize = coreSize * 8;
        return buildThreadFirstExecutor(coreSize, maxSize, 5, TimeUnit.MINUTES, 1 << 16, poolName);
    }

    /**
     * 构建线程优先的线程池
     * <p>
     * 线程池默认是当核心线程数满了后，将任务添加到工作队列中，当工作队列满了之后，再创建线程直到达到最大线程数。
     *
     * <p>
     * 线程优先的线程池，就是在核心线程满了之后，继续创建线程，直到达到最大线程数之后，再把任务添加到工作队列中。
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   空闲线程的空闲时间
     * @param unit            时间单位
     * @param workQueueSize   工作队列容量大小
     * @param poolName        线程池名称
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor buildThreadFirstExecutor(int corePoolSize,
                                                              int maximumPoolSize,
                                                              long keepAliveTime,
                                                              TimeUnit unit,
                                                              int workQueueSize,
                                                              String poolName) {
        // 自定义队列，优先开启更多线程，而不是放入队列
        ThreadPriorityBlockingQueue<Runnable> queue = new ThreadPriorityBlockingQueue<>(workQueueSize);

        // 当线程达到 maximumPoolSize 时会触发拒绝策略，此时将任务 put 到队列中
        RejectedExecutionHandler rejectedExecutionHandler = (runnable, executor) -> {
            try {
                // 任务拒绝时，通过 put 放入队列
                queue.put(runnable);
            } catch (InterruptedException e) {
                LOGGER.warn("{} Queue offer interrupted. ", poolName, e);
                Thread.currentThread().interrupt();
            }
        };

        // 线程工厂
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(poolName + "-%d")
                .setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
                    LOGGER.error("{} catching the uncaught exception, ThreadName: [{}]", poolName, thread.toString(), throwable);
                })
                .build();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize,
                keepAliveTime, unit,
                queue,
                threadFactory,
                rejectedExecutionHandler
        );

        // 设置允许线程池中核心线程被回收
        executor.allowCoreThreadTimeOut(true);

        // 注册线程池监控
        ExecutorManager.registerAndMonitorThreadPoolExecutor(poolName, executor);

        return executor;
    }

    /**
     * 获取返回CPU核数
     *
     * @return 返回CPU核数，默认为8
     */
    public static int getCpuProcessors() {
        return Runtime.getRuntime() != null && Runtime.getRuntime().availableProcessors() > 0 ?
                Runtime.getRuntime().availableProcessors() : 8;
    }

    /**
     * 批量提交异步任务，使用默认的线程池
     *
     * @param tasks 将任务转化为 AsyncTask 批量提交
     */
    public static <T> List<T> batchExecuteAsync(List<AsyncTask<T>> tasks, @NonNull String taskName) {
        return batchExecuteAsync(tasks, BASE_EXECUTOR, taskName);
    }

    /**
     * 批量提交异步任务，执行失败可抛出异常或返回异常编码即可 <br>
     * <p>
     * 需注意提交的异步任务无法控制事务，一般需容忍产生一些垃圾数据的情况下才能使用异步任务，异步任务执行失败将抛出异常，主线程可回滚事务.
     * <p>
     * 异步任务失败后，将取消剩余的任务执行.
     *
     * @param tasks    将任务转化为 AsyncTask 批量提交
     * @param executor 线程池，需自行根据业务场景创建相应的线程池
     * @return 返回执行结果
     */
    public static <T> List<T> batchExecuteAsync(@NonNull List<AsyncTask<T>> tasks, @NonNull ThreadPoolExecutor executor, @NonNull String taskName) {

        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.emptyList();
        }

        int size = tasks.size();

        // 将AsyncTask<T>封装为Callable任务（为什么要封装为Callable任务？因为要在任务执行前后输出日志）
        List<Callable<T>> callables = tasks.stream().map(t -> (Callable<T>) () -> {
            try {
                // 执行异步任务并返回结果（一旦执行出现异常，则在catch块中向上抛出。Throwable是顶层异常，可以捕获一切运行时异常）
                T result = t.doExecute();

                LOGGER.debug("[>>Executor<<] Async task execute success. ThreadName: [{}], BatchTaskName: [{}], SubTaskName: [{}]",
                        Thread.currentThread().getName(), taskName, t.taskName());
                return result;
            } catch (Throwable e) {
                LOGGER.warn("[>>Executor<<] Async task execute error. ThreadName: [{}], BatchTaskName: [{}], SubTaskName: [{}], exception: {}",
                        Thread.currentThread().getName(), taskName, t.taskName(), e.getMessage());
                throw e;
            }
        }).collect(Collectors.toList());

        // CompletionService的特性，一批任务提交到线程池以后，先执行完毕的任务会优先被添加到执行完毕队列中，可以通过completionService.poll获取
        CompletionService<T> completionService = new ExecutorCompletionService<>(executor, new LinkedBlockingQueue<>(size));
        List<Future<T>> futures = new ArrayList<>(size);
        LOGGER.info("[>>Executor<<] Start async tasks, BatchTaskName: [{}], TaskSize: [{}]", taskName, size);

        for (Callable<T> task : callables) {
            // 通过completionService将任务提交给线程池执行
            futures.add(completionService.submit(task));
        }

        // 接收线程任务返回值
        List<T> resultList = new ArrayList<>(size);

        // 接收异步任务返回值（一旦有异步任务执行失败，则取消其他异步任务并抛出异常）
        for (int i = 0; i < size; i++) {
            try {
                // 从完成队列中获取执行完毕的任务(这里默认等待6分钟)
                Future<T> future = completionService.poll(6, TimeUnit.MINUTES);
                if (future != null) {
                    // 收集异步任务返回结果
                    T result = future.get();
                    resultList.add(result);
                    Object log;
                    if (result instanceof Collection) {
                        log = ((Collection<?>) result).size();
                    } else {
                        log = result;
                    }
                    LOGGER.debug("[>>Executor<<] Async task [{}] - [{}] execute success, result: {}", taskName, i, log == null ? "null" : log);
                } else {
                    cancelTask(futures);
                    LOGGER.error("[>>Executor<<] Async task [{}] - [{}] execute timeout, then cancel other tasks.", taskName, i);
                    throw new CommonException(EnhanceConcurrentConstants.ErrorCode.TIMEOUT);
                }
                // ExecutionException在线程任务被抢占或线程任务发生异常时抛出
            } catch (ExecutionException e) {
                LOGGER.warn("[>>Executor<<] Async task [{}] - [{}] execute error, then cancel other tasks.", taskName, i, e);
                cancelTask(futures);
                Throwable throwable = e.getCause();
                if (throwable instanceof CommonException) {
                    throw (CommonException) throwable;
                } else if (throwable instanceof DuplicateKeyException) {
                    throw (DuplicateKeyException) throwable;
                } else {
                    throw new CommonException("error.executorError", e.getCause().getMessage());
                }
            } catch (InterruptedException e) {
                cancelTask(futures);
                // 重置中断标识
                Thread.currentThread().interrupt();
                LOGGER.error("[>>Executor<<] Async task [{}] - [{}] were interrupted.", taskName, i);
                throw new CommonException(EnhanceConcurrentConstants.ErrorCode.ERROR);
            }
        }

        LOGGER.info("[>>Executor<<] Finish async tasks , BatchTaskName: [{}], TaskSize: [{}]", taskName, size);

        // 返回异步任务执行的返回结果集合
        return resultList;
    }

    private static <T> void cancelTask(List<Future<T>> futures) {
        for (Future<T> future : futures) {
            if (!future.isDone()) {
                // 正在运行的任务也去中断
                future.cancel(true);
            }
        }
    }

}
