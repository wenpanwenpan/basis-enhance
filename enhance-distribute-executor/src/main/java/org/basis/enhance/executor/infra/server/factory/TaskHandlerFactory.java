package org.basis.enhance.executor.infra.server.factory;


import org.basis.enhance.executor.infra.server.handler.TaskHandler;

import java.io.Serializable;

/**
 * 任务处理工厂类
 *
 * @author Mr_wenpan@163.com 2021/8/18 9:32 下午
 */
public interface TaskHandlerFactory {

    /**
     * 任务处理器实例
     *
     * @param taskHandlerBeanId 任务处理器实现类全名
     * @return 实例
     */
    TaskHandler<Serializable> newInstance(String taskHandlerBeanId);

}