package org.basis.enhance.pubsub;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 发布订阅处理注解
 *
 * @author Mr_wenpan@163.com 2021/08/31 14:05
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Documented
public @interface PubSubHandler {

    @AliasFor("value")
    String channel();

    @AliasFor("channel")
    String value();

}
