package org.enhance.message.infra.listener;

import org.basis.enhance.event.event.PushRedisEvent;
import org.basis.enhance.event.listener.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 推送Redis事件监听器
 *
 * @author Mr_wenpan@163.com 2022/04/02 12:21
 */
@Component
public class PushRedisListener implements ApplicationListener<PushRedisEvent> {

    @Override
    public void onApplicationEvent(PushRedisEvent event) {
    }

    @Override
    public Class<PushRedisEvent> supportEventType() {

        return PushRedisEvent.class;
    }

}
