package org.basis.enhance.executor.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * spring容器帮助器
 *
 * @author Mr_wenpan@163.com 2021/09/12 20:58
 */
public class ExecutorApplicationContextHelper implements ApplicationContextAware {

    static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }
}
