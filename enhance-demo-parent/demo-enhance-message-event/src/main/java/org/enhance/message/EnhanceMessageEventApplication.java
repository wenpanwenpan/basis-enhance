package org.enhance.message;

import org.basis.enhance.event.annotation.EnableBasisMessageEvent;
import org.basis.enhance.event.event.DefaultMessageEvent;
import org.basis.enhance.event.event.PushRedisEvent;
import org.basis.enhance.event.publisher.EventPublisher;
import org.enhance.message.spring.entity.MutationType;
import org.enhance.message.spring.entity.User;
import org.enhance.message.spring.event.MutationEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 增强消息事件测试启动类
 *
 * @author Mr_wenpan@163.com 2022/4/1 10:30 下午
 */
@EnableBasisMessageEvent
@SpringBootApplication
public class EnhanceMessageEventApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EnhanceMessageEventApplication.class, args);
        EventPublisher publisher = context.getBean(EventPublisher.class);
        publisher.publish(new DefaultMessageEvent());
        publisher.publish(new PushRedisEvent());
        publisher.publishAsync(new DefaultMessageEvent());
        publisher.publishAsync(new PushRedisEvent());

//        ApplicationEventPublisher eventPublisher = context.getBean(ApplicationEventPublisher.class);
        User user = new User();
        user.setName("wenpan");
        user.setAddress("chengdi");
        user.setAge(20);
//        eventPublisher.publishEvent(new MutationEvent<>(user, new MutationType()));
        context.publishEvent(new MutationEvent<>(user, new MutationType()));
    }
}