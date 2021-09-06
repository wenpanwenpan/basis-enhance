package org.basis.enhance.spring.concurrent;

import org.apache.commons.collections4.CollectionUtils;
import org.basis.enhance.spring.helper.ApplicationContextHelper;
import org.basis.enhance.spring.util.ListSplitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 并发执行器
 *
 * @author Mr_wenpan@163.com 2020/2/4 2:27 下午
 */
public class ConcurrentExecutor<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentExecutor.class);
    private static final int DEFAULT_TASK_COUNT = 4;
    private static final int DEFAULT_BATCH_SIZE = 500;
    private static final String THREAD_FINISHED = "FINISHED";

    private final long taskCount;
    private final long batchSize;
    private final List<T> processDataList;
    private final SchedulingTaskExecutor taskExecutor;
    private final AtomicInteger taskCounter;

    public ConcurrentExecutor(List<T> processDataList) {
        this(DEFAULT_TASK_COUNT, DEFAULT_BATCH_SIZE, processDataList,
                ApplicationContextHelper.getContext().getBean("o2ThreadPool", ThreadPoolTaskExecutor.class)
        );
    }

    public ConcurrentExecutor(int taskCount,
                              List<T> processDataList) {
        this(taskCount, DEFAULT_BATCH_SIZE, processDataList,
                ApplicationContextHelper.getContext().getBean("o2ThreadPool", ThreadPoolTaskExecutor.class)
        );
    }

    public ConcurrentExecutor(int taskCount,
                              List<T> processDataList,
                              SchedulingTaskExecutor taskExecutor) {
        this(taskCount, DEFAULT_BATCH_SIZE, processDataList, taskExecutor);
    }

    public ConcurrentExecutor(long taskCount,
                              long batchSize,
                              List<T> processDataList,
                              SchedulingTaskExecutor taskExecutor) {
        this.taskCount = taskCount;
        this.batchSize = batchSize;
        this.processDataList = processDataList;
        this.taskExecutor = taskExecutor;
        taskCounter = new AtomicInteger(0);
    }

    public void invoke(Consumer<T> consumer) {
        if (CollectionUtils.isEmpty(processDataList)) {
            return;
        }

        if (taskCount <= 1 || processDataList.size() <= batchSize) {
            threadExecuter(consumer, processDataList);
            return;
        }

        // 分发任务int
        List<Future<String>> results = new ArrayList<>();
        List<List<T>> subPendingLists = ListSplitUtil.splitList(processDataList, (int) batchSize);
        for (List<T> currentBatchProcess : subPendingLists) {
            results.add(taskRunner(consumer, new ArrayList<>(currentBatchProcess)));
        }

        // 等待调度的线程执行结束
        for (Future<String> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                LOG.error("ConcurrentExecutor invoke erroroccured : ", e);
            }
        }
    }

    private Future<String> taskRunner(Consumer<T> consumer, Collection<T> processVar) {
        while (true) {
            //如果当前线程数量小于配置线程总数
            if (taskCounter.get() < taskCount) {
                return taskExecutor.submit(() -> {
                    taskCounter.incrementAndGet();
                    try {
                        threadExecuter(consumer, processVar);
                    } catch (Exception e) {
                        LOG.error("ConcurrentExecutor taskRunner erroroccured : ", e);
                    } finally {
                        taskCounter.decrementAndGet();
                    }
                    return THREAD_FINISHED;
                });
            }
        }
    }

    private void threadExecuter(Consumer<T> consumer, Collection<T> processVar) {
        for (T processData : processVar) {
            consumer.accept(processData);
        }
    }
}