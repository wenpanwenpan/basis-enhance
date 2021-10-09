package org.basis.enhance.spring.concurrent;

import java.util.UUID;

/**
 * 异步任务
 *
 * @author Mr_wenpan@163.com 2021/10/9 3:08 下午
 */
public interface AsyncTask<T> {

    /**
     * 获取任务名称，默认随机生成，可自己覆盖该方法
     *
     * @return java.lang.String 任务名称
     */
    default String taskName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 执行任务
     *
     * @return T 任务返回结果
     */
    T doExecute();
}