package org.enhance.message.spring.listener;

import lombok.extern.slf4j.Slf4j;
import org.enhance.message.spring.entity.User;
import org.enhance.message.spring.event.MutationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * spring事件监听器
 *
 * @author wenpanfeng 2022/8/2 17:41
 */
@Slf4j
@Component
public class UserDataChangedListener2 {

    @EventListener
    public void userDataChanged(MutationEvent<User> event) {
        log.info("UserDataChangedListener2监听器收到消息event = {}", event);
    }
}