package org.basis.enhance.executor.infra.client;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.executor.domain.entity.Task;
import org.basis.enhance.executor.domain.repository.TaskDataRepository;
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
    private final TaskDataRepository taskDataRepository;
    private final TaskRedisRepository taskRedisRepository;

    MainTask(String mainTaskId,
             TaskRepository taskRepository,
             TaskDataRepository taskDataRepository,
             TaskRedisRepository taskRedisRepository) {
        subTasks = new ArrayList<>();
        this.mainTaskId = mainTaskId;
        this.taskRepository = taskRepository;
        this.taskDataRepository = taskDataRepository;
        this.taskRedisRepository = taskRedisRepository;
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
                task.setId(UUIDUtil.generateUUID());
                task.setMainTaskId(mainTaskId);
                task.setCreationDate(creationDate);
                task.setModifyDate(creationDate);
                // 将任务数据保存起来，方便执行任务的时候取用
                String dataUrl = taskDataRepository.save(task);
                if (!StringUtils.equals(dataUrl, StoneExecutorConstants.TaskDataUrl.NO_DATA_URL)) {
                    // 清空任务数据（即任务数据存放在文件系统中，而不用存放在MongoDB里）
                    task.setData(null);
                }
                task.setDataStore(dataUrl);
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