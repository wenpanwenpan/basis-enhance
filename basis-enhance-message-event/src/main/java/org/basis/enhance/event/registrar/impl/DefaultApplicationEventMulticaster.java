package org.basis.enhance.event.registrar.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.basis.enhance.event.event.BasisEvent;
import org.basis.enhance.event.listener.ApplicationListener;
import org.basis.enhance.event.registrar.ApplicationEventMulticaster;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认事件广播器实现
 *
 * @author Mr_wenpan@163.com 2022/04/01 17:37
 */
public class DefaultApplicationEventMulticaster implements ApplicationEventMulticaster, DisposableBean {

    /**
     * 监听器集合
     */
    public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

    /**
     * 监听器集合
     */
    public final Map<Class<?>, List<ApplicationListener<?>>> applicationListenerMap = new ConcurrentHashMap<>();

    @Override
    public void addApplicationListeners(Set<ApplicationListener<?>> listeners) {
        if (CollectionUtils.isEmpty(listeners)) {
            return;
        }
        // 加锁同步
        synchronized (applicationListeners) {
            for (ApplicationListener<?> listener : listeners) {
                addApplicationListener(listener);
            }
        }
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        if (Objects.isNull(listener)) {
            return;
        }
        // 加锁同步
        synchronized (applicationListeners) {
            Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
            // 若有代理则用代理替换目标对象
            if (singletonTarget instanceof ApplicationListener) {
                applicationListeners.remove(singletonTarget);
            }
            List<ApplicationListener<?>> listenerSet = applicationListenerMap.computeIfAbsent(listener.supportEventType(), (key) -> new ArrayList<>());
            listenerSet.add(listener);
            // 多个监听器按照@Order排序
            AnnotationAwareOrderComparator.sort(listenerSet);
            applicationListeners.add(listener);
        }
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        if (Objects.isNull(listener)) {
            return;
        }
        synchronized (applicationListeners) {
            applicationListeners.remove(listener);
            applicationListenerMap.remove(listener.supportEventType());
        }
    }

    @Override
    public void removeAllListeners() {
        synchronized (applicationListeners) {
            applicationListeners.clear();
            applicationListenerMap.clear();
        }
    }

    @Override
    @SuppressWarnings("all")
    public void multicastEvent(BasisEvent event) {
        if (Objects.isNull(event)) {
            return;
        }
        for (ApplicationListener listener : applicationListeners) {
            // 获取到事件真实类型
            ResolvableType resolvableType = ResolvableType.forInstance(event);
            ResolvableType eventType = ResolvableType.forClass(listener.supportEventType());
            boolean assignableFrom = eventType.isAssignableFrom(resolvableType);
            // 匹配成功才执行方法（spring里就是使用的这种方式）
            if (eventType.isAssignableFrom(resolvableType)) {
                listener.onApplicationEvent(event);
            }
        }
    }

    @Override
    @SuppressWarnings("all")
    public void multicastEventEfficient(BasisEvent event) {
        if (Objects.isNull(event)) {
            return;
        }
        List<ApplicationListener<?>> listeners = applicationListenerMap.get(event.getClass());
        if (CollectionUtils.isEmpty(listeners)) {
            throw new RuntimeException(String.format("According to [%s] unable to find the corresponding listener please check.", event));
        }
        for (ApplicationListener listener : listeners) {
            listener.onApplicationEvent(event);
        }
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     *
     * @throws Exception in case of shutdown errors. Exceptions will get logged
     *                   but not rethrown to allow other beans to release their resources as well.
     */
    @Override
    public void destroy() throws Exception {
        applicationListeners.clear();
        applicationListenerMap.clear();
    }
}
