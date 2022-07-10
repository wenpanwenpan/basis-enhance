package org.basis.enhance.limit.annotation;

import org.basis.enhance.limit.selector.RedisLimitingImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启redis限流组件
 *
 * @author wenpan 2021/07/05 11:44
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(value = {RedisLimitingImportSelector.class})
public @interface EnableRedisLimiting {

}