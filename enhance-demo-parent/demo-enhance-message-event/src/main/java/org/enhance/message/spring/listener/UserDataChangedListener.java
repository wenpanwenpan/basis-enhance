package org.enhance.message.spring.listener;

import lombok.extern.slf4j.Slf4j;
import org.enhance.message.spring.entity.Car;
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
public class UserDataChangedListener {

    @EventListener
    public void userDataChanged(MutationEvent<User> event) {
        log.info("UserDataChangedListener监听器收到消息event = {}", event);
    }

    @EventListener
    public void userDataChanged3(MutationEvent<Car> event) {
        log.info("UserDataChangedListener#userDataChanged3监听器收到消息event = {}", event);
    }
}