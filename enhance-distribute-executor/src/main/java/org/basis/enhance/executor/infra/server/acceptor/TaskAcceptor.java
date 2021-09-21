package org.basis.enhance.executor.infra.server.acceptor;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.basis.enhance.executor.config.property.ExecutorProperties;
import org.basis.enhance.executor.domain.repository.TaskRedisRepository;
import org.basis.enhance.executor.infra.server.context.ExecutorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 任务接收器
 *
 * @author Mr_wenpan@163.com 2021/7/11 9:34 下午
 */
@Data
public final class TaskAcceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TaskAcceptor.class);
    /**
     * 关机标志
     */
    private volatile boolean shutdownFlag;
    /**
     * 所属任务组
     */
    private String taskGroup;
    /**
     * 轮询频率(ms) 默认200ms
     */
    private Long pollingFrequencyMillis;
    /**
     * 任务操作
     */
    private final TaskRedisRepository taskRepository;
    /**
     * 配置属性
     */
    private final ExecutorProperties properties;
    /**
     * 执行上下文
     */
    private final ExecutorContext executorContext;

    public TaskAcceptor(ExecutorContext executorContext) {
        shutdownFlag = false;
        this.executorContext = executorContext;
        taskRepository = executorContext.getTaskRedisRepository();
        properties = executorContext.getProperties();
        taskGroup = properties.getGroup();
        pollingFrequencyMillis = properties.getPollingFrequencyMillis();
    }

    public void accept() {
        // 创建一个线程去抢占任务，需要catch全局异常，不能让抢占线程因为意外信息而中断抢占
        Thread thread = Executors.defaultThreadFactory().newThread(() -> {
            while (!shutdownFlag) {
                try {

                    // todo 有任务就一直抢，没任务则休眠
                    // 如果没有空闲线程则休眠一会儿
                    if (executorContext.getFreeTaskCounter().get() > 0) {
                        Set<ZSetOperations.TypedTuple<String>> tasks = taskRepository.scanTask(taskGroup);
                        LOG.info("扫描得到的任务数量：{}", tasks == null ? 0 : tasks.size());
                        boolean success = true;
                        if (CollectionUtils.isNotEmpty(tasks)) {
                            // 遍历子任务
                            for (ZSetOperations.TypedTuple<String> taskWithScore : tasks) {
                                // 获取子任务ID
                                String task = taskWithScore.getValue();
                                // 抢占任务
                                if (taskRepository.occupyTask(taskGroup, task, executorContext.getNode(),
                                        properties.getTaskLockExpireSeconds())) {

                                    // 抢占成功判断停机标志, 停机释放抢占的任务
                                    if (shutdownFlag) {
                                        taskRepository.releaseTask(taskGroup, task);
                                        break;
                                    }

                                    LOG.info("抢占任务成功，任务信息：{}", task);

                                    success = false;
                                    // 执行任务
                                    executorContext.doWork(taskWithScore);
                                    break;
                                }
                            }
                            // 如果一轮抢下来都没有抢到，那就睡一会儿
                            if (success) {
                                TimeUnit.MILLISECONDS.sleep(pollingFrequencyMillis);
                            }

                        } else {
                            // 如果此次没有扫描到任务，休眠一会儿
                            TimeUnit.MILLISECONDS.sleep(pollingFrequencyMillis);
                        }
                    } else {
                        // 此时没有空闲线程，休眠一会儿
                        TimeUnit.MILLISECONDS.sleep(1);
                    }

                } catch (Exception e) {
                    LOG.error("task acceptor [{}] accept error:", Thread.currentThread().getName(), e);
                }
            }
        });
        thread.setName(THREAD_NAME);
        thread.setDaemon(true);
        thread.start();
    }

    public void shutdown() {
        shutdownFlag = true;
    }

    private static final String THREAD_NAME = "STONE_EXECUTOR_TASK_ACCEPTER";

}