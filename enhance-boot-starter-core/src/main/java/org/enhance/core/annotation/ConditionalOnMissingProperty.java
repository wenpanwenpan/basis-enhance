package org.enhance.core.annotation;

import org.enhance.core.annotation.condition.OnMissingPropertyCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 不存在这个属性时才满足
 *
 * @author wenpan 2022/9/25 12:00 下午
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(OnMissingPropertyCondition.class)
public @interface ConditionalOnMissingProperty {

    /**
     * 属性值，多个值要求同时为空
     *
     * @return 属性配置
     */
    String[] value() default {};
}