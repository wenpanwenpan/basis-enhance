package org.enhance.core.annotation;

import org.enhance.core.annotation.condition.ConditionalOnPropertiesCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * <p>
 * ConditionalOnProperty注解功能补充
 * </p>
 *
 * @author wenpan 2022/09/08 19:07
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(ConditionalOnPropertiesCondition.class)
public @interface ConditionalOnProperties {

    /**
     * 配置前缀
     */
    String prefix();

    /**
     * <p>
     * properties value that need to be present
     * note: that values length must be equal to {@link ConditionalOnProperties#properties()} length
     * </p>
     */
    String[] values();

    /**
     * <p>
     * properties that need to be present
     * note: that properties length must be equal to {@link ConditionalOnProperties#values()}  length
     * </p>
     */
    String[] properties();

    /**
     * <p>
     * true 表示任意匹配一个值
     * false 标识所有值都需要匹配
     * 默认全部properties和values一一匹配
     * </p>
     */
    boolean anyMatch() default false;

    /**
     * match 如果配置文件没有配置property
     */
    boolean matchIfMissing() default false;

}