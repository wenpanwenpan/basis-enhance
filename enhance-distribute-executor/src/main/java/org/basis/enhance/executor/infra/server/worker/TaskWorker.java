package org.basis.enhance.executor.infra.server.worker;

import lombok.Data;
import org.basis.enhance.executor.config.property.ExecutorProperties;
import org.basis.enhance.executor.domain.entity.Task;
import org.basis.enhance.executor.handler.RetryWarnHandler;
import org.basis.enhance.executor.helper.ExecutorApplicationContextHelper;
import org.basis.enhance.executor.infra.constants.StoneExecutorConstants;
import org.basis.enhance.executor.infra.server.context.ExecutorContext;
import org.basis.enhance.executor.infra.server.handler.TaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务执行器
 *
 * @author Mr_wenpan@163.com 2021/8/17 9:28 下午
 */
@Data
public class TaskWorker {

    private static final Logger LOG = LoggerFactory.getLogger(TaskWorker.class);

    /**
     * 线程池
     */
    private final ThreadPoolTaskExecutor executor;

    /**
     * 执行上下文
     */
    private final ExecutorContext executorContext;

    /**
     * 配置属性
     */
    private final ExecutorProperties properties;

    /**
     * 重试告警处理器，可多个，按Order优先级执行
     */
    List<RetryWarnHandler> retryWarnHandlers;

    public TaskWorker(ExecutorContext executorContext) {
        this.executorContext = executorContext;
        properties = executorContext.getProperties();
        executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(properties.getMaxTaskCount());
        executor.setCorePoolSize(properties.getMaxTaskCount());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        Map<String, RetryWarnHandler> beans = ExecutorApplicationContextHelper
                .getApplicationContext().getBeansOfType(RetryWarnHandler.class);
        retryWarnHandlers = new ArrayList<>(beans.values());
        AnnotationAwareOrderComparator.sort(retryWarnHandlers);
    }

    /**
     * 自己new对象时不能用@PostConstruct进行初始化，只有注入容器中的bean才能使用@PostConstruct
     */
//    @PostConstruct
//    private void init() {
//        Map<String, RetryWarnHandler> beans = com.stone.starter.core.helper.ApplicationContextHelper
//                .getContext().getBeansOfType(RetryWarnHandler.class);
//        retryWarnHandlers = new ArrayList<>(beans.values());
//        AnnotationAwareOrderComparator.sort(retryWarnHandlers);
//    }

    /**
     * 手动创建的线程池一定要手动进行关闭，否则容易造成线程泄漏，甚至导致内存溢出
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * 执行子任务
     *
     * @param taskWithScore 子任务
     */
    public void doWork(ZSetOperations.TypedTuple<String> taskWithScore) {
        // 子任务ID
        String taskId = taskWithScore.getValue();
        // 从MongoDB里取出该子任务信息
        Task task = executorContext.getTaskRepository().findById(taskId);
        // 子任务所属组
        String group = properties.getGroup();
        // 获取该任务的得分作为该任务的尝试次数
        int score = Objects.requireNonNull(taskWithScore.getScore()).intValue();

        // 如果从MongoDB里没有获取到任务，则说明这是一条错误的任务
        if (null == task) {
            // 从任务池中清除该任务
            executorContext.removeTaskFromTaskPool(group, taskId);
            return;
        }

        // todo 当尝试超过一定次数则触发告警，告警完毕后应不应该删除或操作任务？？？
        if (score >= properties.getWarnThreshold()) {
            try {
                // 被观察者通知每一个观察者
                retryWarnHandlers.forEach(handler -> handler.doRetryWarn(task));
            } catch (Exception ex) {
                LOG.error("重试超过阈值告警方法执行失败，异常信息：", ex);
            }
            // 告警完毕删除该子任务（应该删除所有子任务 + 主任务）
            executorContext.removeTaskFromTaskPool(group, taskId);
            return;
        }

        task.setAttemptCount(score);

        try {
            // 任务添加到监控器
            executorContext.getTaskMonitor().addToMonitor(taskId);
            // 扣减可用线程量
            executorContext.getFreeTaskCounter().decrementAndGet();
            // 执行任务
            executor.execute(new TaskRunner(task, executorContext));
        } catch (Exception ex) {
            // 回调空闲线程数量
            executorContext.getFreeTaskCounter().incrementAndGet();
            // 移出监控器
            executorContext.getTaskMonitor().removeFromMonitor(taskId);
            LOG.error("execute work error", ex);
        }

    }

    /**
     * 任务执行
     */
    static class TaskRunner implements Runnable {
        private final Task task;
        private final ExecutorContext executorContext;

        TaskRunner(Task task, ExecutorContext executorContext) {
            this.task = task;
            this.executorContext = executorContext;
        }

        @Override
        public void run() {
            String taskId = task.getId();
            boolean isSuccess = true;
            try {
                // 如果该任务被重试了，并且不支持幂等操作则直接抛出异常(即非幂等任务不能重复执行)
                if (task.getAttemptCount() > 0 && !task.getIsIdempotent()) {
                    throw new RuntimeException("execute task node [" + task.getLastRunNode() + "] shutdown and task is not isIdempotent.");
                }

                // 准备任务数据处理器（即从容器中通过bean的ID获取该bean的实例对象）
                TaskHandler<Serializable> taskHandler = executorContext.newInstance(task.getTaskHandlerBeanId());
                if (null == taskHandler) {
                    throw new RuntimeException(taskId + " not fund taskHandler by taskHandlerBeanId: " + task.getTaskHandlerBeanId());
                }

                // 任务执行前
                taskRunning();
                // 读取任务数据
                Serializable data = (Serializable) executorContext.read(task);
                // 执行逻辑
                taskHandler.handler(data);
                // 任务完成记录信息
                taskCompleted();

            } catch (Exception ex) {
                isSuccess = false;
                // 任务出错记录信息，这里也会更新重试次数
                taskError(ex);
                LOG.error("running task [{}] error:{}", taskId, ex);
            } finally {
                // 移出任务监控器
                executorContext.getTaskMonitor().removeFromMonitor(taskId);
                // 归还线程资源
                executorContext.getFreeTaskCounter().incrementAndGet();
                // todo 正常完成任务则从任务池移除，异常则将任务池中该任务的重试次数++（即得分++）等待重试超过阈值时触发告警
                if (isSuccess) {
                    // 任务正常完成 任务从任务池移除
                    LOG.info("任务正常执行完毕，任务数据 ：{}", task);
                    executorContext.taskCompleted(task.getGroup(), taskId, task.getMainTaskId());
                } else {
                    // 任务失败，将重试次数++，并且重试次数要更新到MongoDB（上面已经更新了）
                    LOG.error("任务异常执行完毕，任务数据 ：{}", task);
                    // executorContext.taskFailed(task.getGroup(), taskId);
                }
            }
        }

        /**
         * 任务运行中
         */
        private void taskRunning() {
            task.setLastRunNode(executorContext.getNode());
            task.setStatus(StoneExecutorConstants.TaskStatus.RUNNING);
            task.setModifyDate(new Date());
            // 任务条目更新到MongoDB
            executorContext.getTaskRepository().updateTask(task);
        }

        /**
         * 任务运行完成
         */
        private void taskCompleted() {
            task.setStatus(StoneExecutorConstants.TaskStatus.COMPLETED);
            task.setModifyDate(new Date());
            // 任务条目更新到MongoDB
            executorContext.getTaskRepository().updateTask(task);
        }

        /**
         * 任务出错
         */
        private void taskError(Exception e) {
            task.setStatus(StoneExecutorConstants.TaskStatus.ERROR);
            task.setErrorMsg(e.getMessage());
            task.setModifyDate(new Date());
            // 任务条目更新到MongoDB
            executorContext.getTaskRepository().updateTask(task);
        }

    }
}