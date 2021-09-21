package org.basis.enhance.executor.infra.constants;

/**
 * 分布式任务核心常量
 *
 * @author Mr_wenpan@163.com 2021/04/03 15:30
 */
public interface StoneExecutorConstants {

    /**
     * 任务数据url
     */
    interface TaskDataUrl {
        String NO_DATA_URL = "no-data-url";
    }

    /**
     * 状态
     */
    interface ProcessStatus {
        String PENDING = "PENDING";
        String SUCCESS = "SUCCESS";
        String ERROR = "ERROR";
        String SKIP = "SKIP";
    }

    /**
     * 租户相关信息
     */
    interface TalentInfo {
        Long DEFAULT_ORGANIZATIONID = -1L;
    }

    /**
     * 任务状态
     */
    interface TaskStatus {

        /**
         * 等待运行
         */
        String PENDING = "PENDING";

        /**
         * 运行中
         */
        String RUNNING = "RUNNING";

        /**
         * 已完成
         */
        String COMPLETED = "COMPLETED";

        /**
         * 创建失败
         */
        String CREATE_FAIL = "CREATE_FAIL";

        /**
         * 错误
         */
        String ERROR = "ERROR";

    }

    /**
     * 任务Redis结构
     */
    interface TaskRedisKey {

        /**
         * 任务池  %s = group   元素: 子任务id
         */
        String TASK_POOL_Z_SET = "stone:executor:%s:task_pool";

        /**
         * 正在运行的任务
         */
        String RUNNING_TASK_LOCK = "stone:executor:%s:task_lock:%s";

        /**
         * 任务执行情况 %s = mainTaskId 元素
         */
        String TASK_PROGRESS_Z_SET = "stone:executor:task_progress";

        /**
         * 获取任务池的Key
         *
         * @param group 任务组
         * @return key
         */
        static String getTaskPoolSet(String group) {
            return String.format(TASK_POOL_Z_SET, group);
        }

        /**
         * 获取正在执行的任务的key
         *
         * @param taskKey 任务key
         * @param group   任务组
         * @return key
         */
        static String getTaskLockKey(String group, String taskKey) {
            return String.format(RUNNING_TASK_LOCK, group, taskKey);
        }
    }
}
