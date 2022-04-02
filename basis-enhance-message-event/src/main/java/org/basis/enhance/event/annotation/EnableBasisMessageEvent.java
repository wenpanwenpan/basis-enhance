package org.basis.enhance.event.annotation;

import org.basis.enhance.event.config.MessageEventAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启消息事件注解
 *
 * @author Mr_wenpan@163.com 2022/04/01 22:23
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MessageEventAutoConfiguration.class)
public @interface EnableBasisMessageEvent {

}
