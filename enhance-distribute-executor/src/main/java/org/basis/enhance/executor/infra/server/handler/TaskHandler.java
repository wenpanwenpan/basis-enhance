package org.basis.enhance.executor.infra.server.handler;

import java.io.Serializable;

/**
 * 任务处理类
 *
 * @author Mr_wenpan@163.com 2021/7/25 9:32 下午
 */
public interface TaskHandler<T extends Serializable> {

    /**
     * 任务处理逻辑
     *
     * @param data 待处理的数据
     */
    void handler(T data);
}