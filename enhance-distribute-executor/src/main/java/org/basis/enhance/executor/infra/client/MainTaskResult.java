package org.basis.enhance.executor.infra.client;

import org.basis.enhance.executor.domain.repository.TaskRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 主任务结果
 *
 * @author Mr_wenpan@163.com 2021/8/16 3:36 下午
 */
public class MainTaskResult {
    private static final Logger LOG = LoggerFactory.getLogger(MainTaskResult.class);

    /**
     * 主任务id
     */
    private final String mainTaskId;

    private final TaskRedisRepository taskRedisRepository;

    MainTaskResult(String mainTaskId,
                   TaskRedisRepository taskRedisRepository) {
        this.mainTaskId = mainTaskId;
        this.taskRedisRepository = taskRedisRepository;
    }

    /**
     * 等待任务结束
     *
     * @param wait     轮询频率
     * @param timeUnit 时间单位
     * @return 结果
     */
    public boolean waitTaskFinished(long wait, TimeUnit timeUnit) {
        while (taskRedisRepository.isMainTaskRunning(mainTaskId)) {
            try {
                timeUnit.sleep(wait);
            } catch (Exception e) {
                LOG.error("waiting mainTask{} finished error:{}", mainTaskId, e);
            }
        }
        return true;
    }

    /**
     * 等待任务结束
     *
     * @param wait     轮询频率
     * @param timeUnit 时间单位
     * @param timeout  超时时间
     * @return 结果 超时false 未超时结束返回true
     */
    public boolean waitTaskFinished(long wait, long timeout, TimeUnit timeUnit) {
        long waitTimes = 0;
        while (taskRedisRepository.isMainTaskRunning(mainTaskId)) {
            try {
                waitTimes += wait;
                timeUnit.sleep(wait);
            } catch (Exception e) {
                LOG.error("waiting mainTask{} finished error:{}", mainTaskId, e);
            }
            if (waitTimes >= timeout) {
                return false;
            }
        }
        return true;
    }

    /**
     * 当前任务是否正在运行
     *
     * @return true 正在运行 false 未运行
     */
    public boolean isTaskRunning() {
        return !taskRedisRepository.isMainTaskRunning(mainTaskId);
    }
}