package org.enhance.core.annotation;

import org.enhance.core.annotation.condition.OnExistingPropertyCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * <p>
 * 存在这个属性时才满足
 * </p>
 *
 * @author wenpan 2022/9/25 11:59 上午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnExistingPropertyCondition.class)
public @interface ConditionalOnExistingProperty {

    /**
     * 属性值，多个值要求同时为空
     *
     * @return 属性配置
     */
    String[] value() default {};

}