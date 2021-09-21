package org.basis.enhance.executor.infra.repository.impl;

import org.basis.enhance.executor.config.property.ExecutorProperties;
import org.basis.enhance.executor.domain.entity.Task;
import org.basis.enhance.executor.domain.repository.TaskDataRepository;
import org.basis.enhance.executor.infra.constants.StoneExecutorConstants;

/**
 * 任务数据知识库默认实现，逻辑为保存数据的时候直接存到MongoDB，取数据的时候直接去MongoDB的数据
 *
 * @author Mr_wenpan@163.com 2021/08/17 15:47
 */
public class TaskDataRepositoryImpl implements TaskDataRepository {

    private final ExecutorProperties executorProperties;

    public TaskDataRepositoryImpl(ExecutorProperties executorProperties) {
        this.executorProperties = executorProperties;
    }

    @Override
    public String save(Task task) {
        return StoneExecutorConstants.TaskDataUrl.NO_DATA_URL;
    }

    @Override
    public Object read(Task task) {
        // 默认直接从MongoDB中的条目里读取数据(在使用方没有实现文件上传的情况下)
        return task.getData();
    }

}
