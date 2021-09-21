package org.basis.enhance.executor.infra.server.context;

import lombok.Data;
import org.basis.enhance.executor.config.property.ExecutorProperties;
import org.basis.enhance.executor.domain.entity.Task;
import org.basis.enhance.executor.domain.repository.TaskRedisRepository;
import org.basis.enhance.executor.domain.repository.TaskRepository;
import org.basis.enhance.executor.infra.server.acceptor.TaskAcceptor;
import org.basis.enhance.executor.infra.server.factory.TaskHandlerFactory;
import org.basis.enhance.executor.infra.server.factory.impl.SpringTaskHandlerFactoryImpl;
import org.basis.enhance.executor.infra.server.handler.TaskHandler;
import org.basis.enhance.executor.infra.server.monitor.TaskMonitor;
import org.basis.enhance.executor.infra.server.worker.TaskWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.ZSetOperations;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 执行器上下文统一管理TaskWorker + TaskAcceptor + TaskMonitor
 *
 * @author Mr_wenpan@163.com 2021/8/20 9:29 下午
 */
@Data
public class ExecutorContext implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutorContext.class);

    /**
     * 配置属性
     */
    private final ExecutorProperties properties;

    /**
     * 应用上下文
     */
    private final ApplicationContext applicationContext;

    /**
     * 任务管理redis知识库
     */
    private final TaskRedisRepository taskRedisRepository;

    /**
     * 任务管理知识库
     */
    private final TaskRepository taskRepository;

    /**
     * 空闲的任务线程数
     */
    private volatile AtomicInteger freeTaskCounter;

    /**
     * 任务接待者
     */
    private final TaskAcceptor taskAccepter;

    /**
     * 任务执行者
     */
    private final TaskWorker taskWorker;

    /**
     * 任务监控
     */
    private final TaskMonitor taskMonitor;

    /**
     * 任务处理器工厂
     */
    private final TaskHandlerFactory taskHandlerFactory;

    /**
     * 当前节点信息
     */
    @Value("${spring.cloud.client.ip-address:default}")
    private String node;

    public ExecutorContext(ExecutorProperties properties,
                           TaskRepository taskRepository,
                           ApplicationContext applicationContext,
                           TaskRedisRepository taskRedisRepository) {
        this.properties = properties;
        this.taskRepository = taskRepository;
        this.applicationContext = applicationContext;
        this.taskRedisRepository = taskRedisRepository;
        freeTaskCounter = new AtomicInteger(this.properties.getMaxTaskCount());
        taskWorker = new TaskWorker(this);
        taskAccepter = new TaskAcceptor(this);
        taskMonitor = new TaskMonitor(this);
        taskHandlerFactory = new SpringTaskHandlerFactoryImpl(this.applicationContext);
    }

    /**
     * 关机时调用destroy方法通知监控线程和任务线程关闭
     */
    @Override
    public void destroy() throws Exception {
        LOG.info("系统关机destroy方法被吊起，即将关闭相关资源...");
        taskAccepter.shutdown();
        taskWorker.shutdown();
        taskMonitor.shutdown();
    }

    /**
     * 开启分布式任务
     */
    public void start() {
        // 开启任务监控
        taskMonitor.start();
        // 开启任务抢占
        taskAccepter.accept();
    }

    // 门面模式代码设计 + 迪米特法则，整合taskRedisRepository + taskRepository三个子系统

    public void doWork(ZSetOperations.TypedTuple<String> taskWithScore) {
        taskWorker.doWork(taskWithScore);
    }

    public void removeTaskFromTaskPool(String group, String taskId) {
        taskRedisRepository.removeTaskFromTaskPool(group, taskId);
    }

    public void taskCompleted(String group, String taskKey, String mainTask) {
        taskRedisRepository.taskCompleted(group, taskKey, mainTask);
    }

    public void taskFailed(String group, String subTaskId) {
        taskRedisRepository.taskFailed(group, subTaskId);
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return 任务
     */
    public Task findById(String id) {
        return taskRepository.findById(id);
    }

    /**
     * 更新task
     *
     * @param task 任务数据
     */
    void updateTask(Task task) {
        taskRepository.updateTask(task);
    }

    /**
     * 删除任务
     *
     * @param task 任务
     */
    void deleteTask(Task task) {
        taskRepository.deleteTask(task);
    }

    /**
     * 批量创建任务
     *
     * @param tasks 任务
     */
    void insert(Collection<Task> tasks) {
        taskRepository.insert(tasks);
    }

    /**
     * 任务处理器实例
     *
     * @param taskHandlerBeanId 任务处理器实现类全名
     * @return 实例
     */
    public TaskHandler<Serializable> newInstance(String taskHandlerBeanId) {
        return taskHandlerFactory.newInstance(taskHandlerBeanId);
    }

}