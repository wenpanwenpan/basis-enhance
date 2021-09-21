package org.basis.enhance.executor.infra.client;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.basis.enhance.executor.config.property.ExecutorProperties;
import org.basis.enhance.executor.domain.entity.Task;
import org.basis.enhance.executor.domain.repository.TaskRedisRepository;
import org.basis.enhance.executor.domain.repository.TaskRepository;
import org.basis.enhance.executor.infra.constants.StoneExecutorConstants;
import org.basis.enhance.executor.infra.util.UUIDUtil;

import java.util.*;

/**
 * 主任务
 *
 * @author Mr_wenpan@163.com 2021/8/15 3:34 下午
 */
@Data
public class MainTask {

    /**
     * 主任务id
     */
    private final String mainTaskId;

    /**
     * 子任务列表
     */
    private final List<Task> subTasks;

    private final TaskRepository taskRepository;

    private final TaskRedisRepository taskRedisRepository;

    private final ExecutorProperties executorProperties;

    MainTask(String mainTaskId, ExecutorProperties executorProperties,
             TaskRepository taskRepository, TaskRedisRepository taskRedisRepository) {
        subTasks = new ArrayList<>();
        this.mainTaskId = mainTaskId;
        this.taskRepository = taskRepository;
        this.taskRedisRepository = taskRedisRepository;
        this.executorProperties = executorProperties;
    }

    /**
     * 添加子任务
     *
     * @param task 子任务
     * @return 主任务
     */
    public MainTask addSubTask(Task task) {
        subTasks.add(task);
        return this;
    }

    /**
     * 提交任务
     *
     * @return 主任务
     */
    public MainTaskResult submit() {
        final Long organizationId = StoneExecutorConstants.TalentInfo.DEFAULT_ORGANIZATIONID;
        if (CollectionUtils.isNotEmpty(subTasks)) {
            Map<String, String> taskWithGroup = new LinkedHashMap<>();
            Date creationDate = new Date();
            for (Task task : subTasks) {
                task.setGroup(executorProperties.getGroup());
                task.setId(UUIDUtil.generateUUID());
                task.setMainTaskId(mainTaskId);
                task.setCreationDate(creationDate);
                task.setModifyDate(creationDate);
                task.setOrganizationId(organizationId);
                // 子任务ID为key，子任务所属的组为value
                taskWithGroup.put(task.getId(), task.getGroup());
            }

            // 出入条目到mongo
            taskRepository.insert(subTasks);
            // 任务提交到redis任务池
            taskRedisRepository.createTasks(mainTaskId, taskWithGroup);
        }
        return new MainTaskResult(mainTaskId, taskRedisRepository);
    }
}