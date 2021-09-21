package org.basis.enhance.executor.domain.repository;


import org.basis.enhance.executor.domain.entity.Task;

/**
 * 任务数据操作知识库
 *
 * @author Mr_wenpan@163.com 2021/8/17 3:32 下午
 */
public interface TaskDataRepository {

    /**
     * 保存数据
     *
     * @param task 数据
     * @return 数据标识
     */
    String save(Task task);

    /**
     * 读取数据
     *
     * @param task 任务
     * @return 结果
     */
    Object read(Task task);
}