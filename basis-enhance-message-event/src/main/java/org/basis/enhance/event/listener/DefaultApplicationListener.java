package org.basis.enhance.event.listener;

import org.basis.enhance.event.event.DefaultMessageEvent;

/**
 * 默认消息监听器实现
 *
 * @author Mr_wenpan@163.com 2022/04/01 18:06
 */
public class DefaultApplicationListener implements ApplicationListener<DefaultMessageEvent> {

    @Override
    public void onApplicationEvent(DefaultMessageEvent event) {
        System.out.println(Thread.currentThread().getName() + "---->" + getClass().getName() + " 执行消息啦，event = " + event);
    }

    /**
     * 支持的事件类型
     *
     * @return java.lang.Class<DefaultMessageEvent> 事件类型
     * @author Mr_wenpan@163.com 2022/4/1 7:17 下午
     */
    @Override
    public Class<DefaultMessageEvent> supportEventType() {
        return DefaultMessageEvent.class;
    }
}
