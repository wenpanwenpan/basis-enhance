package org.basis.enhance.executor.infra.repository.impl;

import org.basis.enhance.executor.domain.repository.TaskRedisRepository;
import org.basis.enhance.executor.helper.FastJsonHelper;
import org.basis.enhance.executor.infra.constants.StoneExecutorConstants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 任务操作知识库实现
 *
 * @author Mr_wenpan@163.com 2021/08/10 15:43
 */
public class TaskRedisRepositoryImpl implements TaskRedisRepository {

    private final StringRedisTemplate redisTemplate;

    public TaskRedisRepositoryImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isMainTaskRunning(String mainTaskId) {
        return false;
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> scanTask(String group) {
        String taskPoolSet = StoneExecutorConstants.TaskRedisKey.getTaskPoolSet(group);
        return redisTemplate.boundZSetOps(taskPoolSet).rangeByScoreWithScores(0, Double.MAX_VALUE);
    }

    @Override
    public void removeTaskFromTaskPool(String group, String taskId) {
        String taskPoolSet = StoneExecutorConstants.TaskRedisKey.getTaskPoolSet(group);
        redisTemplate.boundZSetOps(taskPoolSet).remove(taskId);
    }

    @Override
    public void createTasks(String mainTask, Map<String, String> tasksWithGroup) {
        final String taskPoolSetKey = StoneExecutorConstants.TaskRedisKey.TASK_POOL_Z_SET;
        final String mainTaskProgress = StoneExecutorConstants.TaskRedisKey.TASK_PROGRESS_Z_SET;
        taskOperation(TASK_CREATE_SCRIPT, FastJsonHelper.objectToString(tasksWithGroup),
                taskPoolSetKey, mainTaskProgress, mainTask, String.valueOf(tasksWithGroup.size()));
    }

    @Override
    public void luaTest() {
        taskOperation(TEST_SCRIPT);
        System.out.println("luaTest脚本执行成功");
    }

    @Override
    public boolean occupyTask(String group, String taskKey, String node, Integer expireSeconds) {
        String taskLock = StoneExecutorConstants.TaskRedisKey.getTaskLockKey(group, taskKey);
        String taskPoolSet = StoneExecutorConstants.TaskRedisKey.getTaskPoolSet(group);
        return taskOperation(TASK_OCCUPY_SCRIPT, taskLock, node, String.valueOf(expireSeconds), taskPoolSet, taskKey);
    }

    @Override
    public void releaseTask(String group, String taskKey) {
        String taskLock = StoneExecutorConstants.TaskRedisKey.getTaskLockKey(group, taskKey);
        String taskPoolSet = StoneExecutorConstants.TaskRedisKey.getTaskPoolSet(group);
        taskOperation(TASK_RELEASE_SCRIPT, taskLock, taskPoolSet, taskKey);
    }

    @Override
    public void taskCompleted(String group, String taskKey, String mainTask) {
        final String mainTaskProgress = StoneExecutorConstants.TaskRedisKey.TASK_PROGRESS_Z_SET;
        String taskLock = StoneExecutorConstants.TaskRedisKey.getTaskLockKey(group, taskKey);
        String taskPoolSet = StoneExecutorConstants.TaskRedisKey.getTaskPoolSet(group);
        taskOperation(TASK_COMPLETED_SCRIPT, mainTaskProgress, mainTask, taskLock, taskPoolSet, taskKey);
    }

    @Override
    public void taskFailed(String group, String subTaskId) {
        String taskPoolSet = StoneExecutorConstants.TaskRedisKey.getTaskPoolSet(group);
        // 子任务被执行次数++，返回++后的结果
        Double aDouble = redisTemplate.opsForZSet().incrementScore(taskPoolSet, subTaskId, 1);
    }

    @Override
    public void expireTaskLock(String group, String task, Integer taskLockExpireSeconds) {
        String taskLock = StoneExecutorConstants.TaskRedisKey.getTaskLockKey(group, task);
        redisTemplate.expire(taskLock, taskLockExpireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 任务操作
     *
     * @param script 脚本
     * @param params 参数
     * @return 结果
     */
    private <T> T taskOperation(RedisScript<T> script, List<String> keys, Object... params) {
        return redisTemplate.execute(script, keys, params);
    }

    /**
     * 任务操作
     *
     * @param script 脚本
     * @param params 参数
     * @return 结果
     */
    private <T> T taskOperation(RedisScript<T> script, Object... params) {
        return taskOperation(script, null, params);
    }

    /**
     * 任务创建脚本
     */
    private static final String CREATE_TASK_LUA = "script/lua/create_task.lua";

    /**
     * 测试lua脚本
     */
    private static final String TEST_LUA = "script/lua/test.lua";

    /**
     * 任务占用脚本
     */
    private static final String OCCUPY_TASK_LUA = "script/lua/occupy_task.lua";

    /**
     * 任务释放脚本
     */
    private static final String RELEASE_TASK_LUA = "script/lua/release_task.lua";

    /**
     * 任务完成
     */
    private static final String COMPLETED_TASK_LUA = "script/lua/completed_task.lua";

    /**
     * 任务创建
     */
    private static final DefaultRedisScript<?> TASK_CREATE_SCRIPT;

    /**
     * lua脚本测试
     */
    private static final DefaultRedisScript<?> TEST_SCRIPT;

    /**
     * 任务占用
     */
    private static final DefaultRedisScript<Boolean> TASK_OCCUPY_SCRIPT;

    /**
     * 任务释放
     */
    private static final DefaultRedisScript<?> TASK_RELEASE_SCRIPT;

    /**
     * 任务完成
     */
    private static final DefaultRedisScript<?> TASK_COMPLETED_SCRIPT;

    static {

        TEST_SCRIPT = new DefaultRedisScript<>();
        TEST_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(TEST_LUA)));

        TASK_CREATE_SCRIPT = new DefaultRedisScript<>();
        TASK_CREATE_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(CREATE_TASK_LUA)));

        TASK_OCCUPY_SCRIPT = new DefaultRedisScript<>();
        TASK_OCCUPY_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(OCCUPY_TASK_LUA)));
        TASK_OCCUPY_SCRIPT.setResultType(Boolean.class);

        TASK_RELEASE_SCRIPT = new DefaultRedisScript<>();
        TASK_RELEASE_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(RELEASE_TASK_LUA)));

        TASK_COMPLETED_SCRIPT = new DefaultRedisScript<>();
        TASK_COMPLETED_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(COMPLETED_TASK_LUA)));
    }
}
