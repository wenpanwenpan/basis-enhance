package org.basis.enhance.executor.infra.repository.impl;

import org.basis.enhance.executor.domain.entity.Task;
import org.basis.enhance.executor.domain.repository.TaskRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;

/**
 * 任务知识库实现
 *
 * @author Mr_wenpan@163.com 2021/08/14 15:41
 */
public class TaskRepositoryImpl implements TaskRepository {

    private final MongoTemplate mongoTemplate;

    public TaskRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Task findById(String id) {
        return mongoTemplate.findById(id, Task.class);
    }

    @Override
    public void updateTask(Task task) {
        mongoTemplate.save(task);
    }

    @Override
    public void deleteTask(Task task) {
        mongoTemplate.remove(task);
    }

    @Override
    public void insert(Collection<Task> tasks) {
        mongoTemplate.insertAll(tasks);
    }

}
