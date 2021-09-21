package org.basis.enhance.executor.infra.server.factory.impl;

import org.basis.enhance.executor.infra.server.factory.TaskHandlerFactory;
import org.basis.enhance.executor.infra.server.handler.TaskHandler;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;

/**
 * 任务处理工厂实现
 *
 * @author Mr_wenpan@163.com 2021/8/17 9:35 下午
 */
public class SpringTaskHandlerFactoryImpl implements TaskHandlerFactory {

    private final ApplicationContext applicationContext;

    public SpringTaskHandlerFactoryImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public TaskHandler<Serializable> newInstance(String taskHandlerBeanId) {

        try {
            return applicationContext.getBean(taskHandlerBeanId, TaskHandler.class);
        } catch (Exception ex) {
            return null;
        }
    }
}