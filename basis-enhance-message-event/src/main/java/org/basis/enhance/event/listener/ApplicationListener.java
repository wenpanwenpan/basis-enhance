package org.basis.enhance.event.listener;

import org.basis.enhance.event.event.BasisEvent;

/**
 * 事件监听器接口
 *
 * @author Mr_wenpan@163.com 2022/4/1 5:20 下午
 */
public interface ApplicationListener<E extends BasisEvent> extends EventListener {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    void onApplicationEvent(E event);

    /**
     * 获取事件类型
     *
     * @return java.lang.Class<E> 事件类型
     * @author Mr_wenpan@163.com 2022/4/1 7:17 下午
     */
    Class<E> supportEventType();

}