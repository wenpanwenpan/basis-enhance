package org.basis.enhance.limit.annotation;

import java.lang.annotation.*;

/**
 * 滑动窗口限流注解
 *
 * @author wenpanfeng 2022/7/4 21:45
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SlideWindowLimit {

    /**
     * 限流key
     */
    String limitKey() default "";

    /**
     * 限流最大请求数
     */
    int maxRequest() default 10;

    /**
     * 一个时间窗口(单位：秒)
     */
    int timeRequest() default 1;

}