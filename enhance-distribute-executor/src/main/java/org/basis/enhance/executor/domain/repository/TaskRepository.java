package org.basis.enhance.executor.domain.repository;


import org.basis.enhance.executor.domain.entity.Task;

import java.util.Collection;

/**
 * 任务知识库
 *
 * @author Mr_wenpan@163.com 2021/8/14 3:27 下午
 */
public interface TaskRepository {

    /**
     * 通过id查询
     *
     * @param id id
     * @return 任务
     */
    Task findById(String id);

    /**
     * 更新task
     *
     * @param task 任务数据
     */
    void updateTask(Task task);

    /**
     * 删除任务
     *
     * @param task 任务
     */
    void deleteTask(Task task);

    /**
     * 批量创建任务
     *
     * @param tasks 任务
     */
    void insert(Collection<Task> tasks);
}