package org.basis.enhance.executor.domain.repository;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Map;
import java.util.Set;

/**
 * 任务操作知识库
 *
 * @author Mr_wenpan@163.com 2021/8/10 3:32 下午
 */
public interface TaskRedisRepository {

    /**
     * 主任务是否正在运行
     *
     * @param mainTaskId 主任务
     * @return true正在运行 false未运行
     */
    boolean isMainTaskRunning(String mainTaskId);

    /**
     * 扫描任务并返回合适的任务
     *
     * @param group 任务组
     * @return 扫描结果
     */
    Set<ZSetOperations.TypedTuple<String>> scanTask(String group);

    /**
     * 从任务池中移除任务
     *
     * @param group  任务组
     * @param taskId 任务id
     */
    void removeTaskFromTaskPool(String group, String taskId);

    /**
     * 创建任务
     *
     * @param mainTask       主任务
     * @param tasksWithGroup 子任务及其所属任务组
     */
    void createTasks(String mainTask, Map<String, String> tasksWithGroup);

    /**
     * lua脚本执行测试
     *
     * @author Mr_wenpan@163.com 2021/9/2 7:14 下午
     */
    void luaTest();

    /**
     * 抢占任务
     *
     * @param group         任务组
     * @param taskKey       任务key
     * @param node          节点信息
     * @param expireSeconds 占用时间
     * @return 抢占结果
     */
    boolean occupyTask(String group, String taskKey, String node, Integer expireSeconds);

    /**
     * 释放被抢占的任务
     *
     * @param group   任务组
     * @param taskKey 任务key
     */
    void releaseTask(String group, String taskKey);

    /**
     * 任务完成
     *
     * @param group    任务组
     * @param taskKey  任务key
     * @param mainTask 主任务id
     */
    void taskCompleted(String group, String taskKey, String mainTask);

    /**
     * 任务执行失败
     *
     * @param group     子任务所属组
     * @param subTaskId 子任务的id
     * @author Mr_wenpan@163.com 2021/9/2 9:22 下午
     */
    void taskFailed(String group, String subTaskId);

    /**
     * 生效
     *
     * @param group                 任务组
     * @param task                  任务
     * @param taskLockExpireSeconds 存活时间
     */
    void expireTaskLock(String group, String task, Integer taskLockExpireSeconds);
}