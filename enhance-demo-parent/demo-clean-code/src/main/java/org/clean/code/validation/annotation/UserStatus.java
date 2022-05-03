package org.clean.code.validation.annotation;

import org.clean.code.validation.validator.UserStatusValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用户状态校验自定义注解
 * Accepts {@code Integer}. 只能作用于Integer属性
 *
 * @author Mr_wenpan@163.com 2022/05/03 15:30
 */
@Documented
// 将注解和他对应的校验器进行绑定
@Constraint(validatedBy = {UserStatusValidator.class})
@Target({FIELD})
@Retention(RUNTIME)
public @interface UserStatus {

    String message() default "status 只能是 1000，1001，1002";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
