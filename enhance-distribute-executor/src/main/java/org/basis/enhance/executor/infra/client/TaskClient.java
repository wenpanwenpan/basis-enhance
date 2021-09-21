package org.basis.enhance.executor.infra.client;

import org.basis.enhance.executor.domain.repository.TaskDataRepository;
import org.basis.enhance.executor.domain.repository.TaskRedisRepository;
import org.basis.enhance.executor.domain.repository.TaskRepository;
import org.basis.enhance.executor.infra.util.UUIDUtil;

/**
 * 分布式任务客户端
 *
 * @author Mr_wenpan@163.com 2021/8/15 3:26 下午
 */
public class TaskClient {

    private final TaskRepository taskRepository;
    private final TaskDataRepository taskDataRepository;
    private final TaskRedisRepository taskRedisRepository;

    public TaskClient(TaskRepository taskRepository,
                      TaskDataRepository taskDataRepository,
                      TaskRedisRepository taskRedisRepository) {
        this.taskRepository = taskRepository;
        this.taskDataRepository = taskDataRepository;
        this.taskRedisRepository = taskRedisRepository;
    }

    /**
     * 创建主任务
     *
     * @return 主任务
     */
    public MainTask createMainTask() {
        String uuid = UUIDUtil.generateUUID();
        System.out.println("主任务UUID = " + uuid);
        return new MainTask(uuid, taskRepository, taskDataRepository, taskRedisRepository);
    }

}