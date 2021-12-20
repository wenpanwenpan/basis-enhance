package org.basis.enhance.mongo.annotation;

import org.basis.enhance.mongo.config.EnhanceMongoMultiSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启MongoDB多数据源
 *
 * @author Mr_wenpan@163.com 2021/12/20 10:48 下午
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({EnhanceMongoMultiSourceAutoConfiguration.class})
public @interface EnableMongoMultiSource {

}
