package org.clean.code.validation.util;

import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 验证工具类
 *
 * @author Mr_wenpan@163.com 2022/05/03 14:45
 */
public class ValidationUtil {

    /**
     * 非快速失败的校验器
     */
    public static Validator validator;
    /**
     * 快速失败的校验器
     */
    private static Validator failFastValidator;
    /**
     * 校验入参或返回值的
     */
    private static ExecutableValidator executableValidator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        failFastValidator = Validation.byProvider(HibernateValidator.class)
                .configure()
                // 配置快速失败
                .failFast(true)
                .buildValidatorFactory()
                .getValidator();
        // 校验入参或返回值的
        executableValidator = validator.forExecutables();
    }

    /**
     * 校验非bean
     */
    public static <T> List<String> validNotBean(T object,
                                                Method method,
                                                Object[] parameterValues,
                                                Class<?>... groups) {
        Set<ConstraintViolation<T>> set = executableValidator.validateParameters(object, method, parameterValues, groups);
        return set.stream().map(v -> "属性："
                + v.getPropertyPath() + ",属性值："
                + v.getInvalidValue() + ",校验不通过的提示信息: "
                + v.getMessage()).collect(Collectors.toList());
    }

    /**
     * 校验对象（非快速失败）
     */
    public static <T> List<String> valid(T obj, Class<?>... groups) {
        // 校验对象，如果obj中有未通过的校验，则将会把错误信息返回到set中
        Set<ConstraintViolation<T>> set = validator.validate(obj, groups);
        return set.stream().map(v -> "属性："
                + v.getPropertyPath() + ",属性值："
                + v.getInvalidValue() + ",校验不通过的提示信息: "
//                + "消息模板："
                // 获取消息模板（el表达式还未被解析成具体值的时候）
//                + v.getMessageTemplate()
                + v.getMessage()).collect(Collectors.toList());
    }

    /**
     * 校验对象(快速失败，一旦遇到一个不满足条件的字段则立即返回，不再继续走后面的校验了)
     */
    public static <T> List<String> validFailFast(T obj, Class<?>... groups) {
        // 校验对象，如果obj中有未通过的校验，则将会把错误信息返回到set中
        Set<ConstraintViolation<T>> set = failFastValidator.validate(obj, groups);
        return set.stream().map(v -> "属性："
                + v.getPropertyPath() + ",属性值："
                + v.getInvalidValue() + ",校验不通过的提示信息: "
                + v.getMessage()).collect(Collectors.toList());
    }
}
