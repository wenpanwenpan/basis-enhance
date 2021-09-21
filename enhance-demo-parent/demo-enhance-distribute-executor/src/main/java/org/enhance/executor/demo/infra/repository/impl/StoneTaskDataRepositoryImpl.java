package org.enhance.executor.demo.infra.repository.impl;

import lombok.RequiredArgsConstructor;
import org.basis.enhance.executor.domain.entity.Task;
import org.basis.enhance.executor.domain.repository.TaskDataRepository;
import org.enhance.executor.demo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务数据操作知识库实现，这里可以将任务数据上传到自己指定的文件服务器，并返回取该数据的url
 *
 * @author Mr_wenpan@163.com 2021/08/20 13:30
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoneTaskDataRepositoryImpl implements TaskDataRepository {

    private final FileService fileService;

    @Override
    public String save(Task task) {
        return "no-data-url";
    }

    @Override
    public Object read(Task task) {

        return task.getData();
    }

}
