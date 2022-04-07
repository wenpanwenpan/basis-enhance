package org.basis.enhance.event.registrar;

import org.basis.enhance.event.event.BasisEvent;
import org.basis.enhance.event.listener.ApplicationListener;

import java.util.Set;

/**
 * 事件广播器接口
 *
 * @author Mr_wenpan@163.com 2022/04/01 17:26
 */
public interface ApplicationEventMulticaster {

    /**
     * Add a listener to be notified of all events.
     *
     * @param listener the listener to add
     * @see #removeApplicationListener(ApplicationListener)
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * Add all listener to be notified of all events.
     *
     * @param listeners the listeners to add
     * @see #removeApplicationListener(ApplicationListener)
     */
    void addApplicationListeners(Set<ApplicationListener<?>> listeners);

    /**
     * 从监听器列表中删除一个监听器
     *
     * @param listener the listener to remove
     * @see #addApplicationListener(ApplicationListener)
     */
    void removeApplicationListener(ApplicationListener<?> listener);

    /**
     * 移除所有的监听器
     */
    void removeAllListeners();

    /**
     * 广播事件
     *
     * @param event the event to multicast
     */
    void multicastEvent(BasisEvent event);

    /**
     * 高效的广播事件方法
     *
     * @param event 事件
     * @author Mr_wenpan@163.com 2022/4/1 8:14 下午
     */
    void multicastEventEfficient(BasisEvent event);

}
