package org.basis.enhance.limit.annotation;

import java.lang.annotation.*;

/**
 * 令牌桶限流注解
 *
 * @author wenpanfeng 2021/07/04 14:06
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TokenBucketLimit {

    /**
     * 限流key
     */
    String limitKey() default "";

    /**
     * 桶容量
     */
    int capacity() default 10;

    /**
     * 许可数
     */
    int permits() default 1;

    /**
     * 令牌流入速率
     */
    double rate() default 1;
}