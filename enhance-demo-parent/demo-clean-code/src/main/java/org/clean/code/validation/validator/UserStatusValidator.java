package org.clean.code.validation.validator;

import org.clean.code.validation.annotation.UserStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;

/**
 * 用户状态自定义注解校验器
 * 该校验器该怎么绑定约束注解呢？必须要先实现ConstraintValidator类
 *
 * @author Mr_wenpan@163.com 2022/05/03 15:31
 */
public class UserStatusValidator implements ConstraintValidator<UserStatus, Integer> {

    @Override
    public void initialize(UserStatus constraintAnnotation) {
        // do nothing
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        HashSet<Integer> set = new HashSet<>();
        set.add(1000);
        set.add(1001);
        set.add(1002);
        return set.contains(value);
    }
}
