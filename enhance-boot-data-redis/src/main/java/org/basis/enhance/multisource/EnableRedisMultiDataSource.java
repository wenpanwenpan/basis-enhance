package org.basis.enhance.multisource;

import java.lang.annotation.*;

/**
 * 开启Redis多数据源
 *
 * @author Mr_wenpan@163.com 2021/8/24 9:33 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
// @Import(RedisMultiDataSourceRegistrar.class)
public @interface EnableRedisMultiDataSource {

}