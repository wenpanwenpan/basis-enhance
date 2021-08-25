package org.basis.enhance.handler;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 队列注解
 *
 * @author Mr_wenpan@163.com 2021/08/24 22:38
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface QueueHandler {

    String value();
}
