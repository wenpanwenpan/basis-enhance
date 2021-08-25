package org.basis.enhance.mybatis.annotation;

import org.basis.enhance.mybatis.infra.enums.IdGeneratetAlgorithmType;

import java.lang.annotation.*;


/**
 * 自动生成ID注解
 *
 * @author Mr_wenpan@163.com 2021/8/13 10:36 下午
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoId {
    /**
     * 主键名
     */
    String primaryKey();

    /**
     * 支持的主键算法类型
     */
    IdGeneratetAlgorithmType type() default IdGeneratetAlgorithmType.SNOWFLAKE;

}
