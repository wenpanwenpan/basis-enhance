package org.basis.enhance.datasource.annotation;

import java.lang.annotation.*;

/**
 * 动态数据源注解
 *
 * @author Mr_wenpan@163.com 2022/04/06 23:21
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicDataSource {

    /**
     * 具体数据源名称
     *
     * @return 数据源名称
     */
    String value();
}
