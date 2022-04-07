package org.basis.enhance.datasource.annotation;

import org.basis.enhance.datasource.config.DynamicDataSourceImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启动态数据源注解
 *
 * @author Mr_wenpan@163.com 2022/04/06 23:25
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(DynamicDataSourceImportSelector.class)
public @interface EnableDynamicDataSource {

}
