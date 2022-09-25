package org.basis.enhance.groovy.annotation;

import org.basis.enhance.groovy.annotation.condition.OnExistingPropertyCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 存在这个属性时才满足
 *
 * @author wenpan 2022/9/18 5:55 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnExistingPropertyCondition.class)
public @interface ConditionalOnExistingProperty {

    /**
     * 属性
     */
    String property();

    /**
     * 属性值
     */
    String value() default "";

}
