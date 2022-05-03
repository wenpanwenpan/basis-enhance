package org.clean.code.validation.util;

import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 集合验证，JSR-303中不认为集合(eg. List<T>;)是一个Bean，因此无法使用@Valid注解来验证内部元素
 * 如果需要使用@Valid注解验证List，需要提供一个Bean包含List对象，验证Bean来完成List校验，但这会改变json结构
 * </p>
 *
 * @author Mr_wenpan@163.com 2022/4/15 5:22 下午
 */
public class ValidUtils {
    private ValidUtils() {
    }

    private static final ValidationResult DEFAULT_PROCESS = new ValidationResult() {
    };

    /**
     * 验证单个元素，使用默认异常信息处理
     *
     * @param validator 验证器
     * @param object    Bean
     * @param groups    验证组
     * @param <T>       Bean 泛型
     */
    public static <T> void valid(Validator validator, T object, Class<?>... groups) {
        valid(validator, object, DEFAULT_PROCESS, groups);
    }

    /**
     * 验证单个元素，使用默认异常信息处理
     *
     * @param validator 验证器
     * @param object    Bean
     * @param process   异常信息处理
     * @param groups    验证组
     * @param <T>       Bean 泛型
     */
    public static <T> void valid(Validator validator, T object, ValidationResult process, Class<?>... groups) {
        Set<ConstraintViolation<T>> violationSet;
        // 这里 groups 如果不传，那么这种多参数的会被变为空数组，而并不是null
        if (groups == null || groups.length < 1) {
            violationSet = validator.validate(object);
        } else {
            violationSet = validator.validate(object, groups);
        }
        if (process != null) {
            process.process(violationSet);
        }
    }

    /**
     * 迭代验证集合元素，使用默认异常信息处理
     *
     * @param validator  验证器
     * @param collection Bean集合
     * @param groups     验证组
     * @param <T>        Bean 泛型
     */
    public static <T> void valid(Validator validator, Collection<T> collection, Class<?>... groups) {
        valid(validator, collection, DEFAULT_PROCESS, groups);
    }

    /**
     * 迭代验证集合元素，使用自定义异常信息处理
     *
     * @param validator  验证器
     * @param collection Bean集合
     * @param process    异常信息处理
     * @param groups     验证组
     * @param <T>        Bean 泛型
     */
    public static <T> void valid(Validator validator, Collection<T> collection, ValidationResult process, Class<?>... groups) {
        if (CollectionUtils.isEmpty(collection)) {
            return;
        }
        int index = 0;
        Map<Integer, Set<ConstraintViolation<T>>> resultMap = new HashMap<>(collection.size());
        for (T obj : collection) {
            Set<ConstraintViolation<T>> violationSet;
            // 这里 groups 如果不传，那么这种多参数的会被变为空数组，而并不是null
            if (groups == null || groups.length < 1) {
                violationSet = validator.validate(obj);
            } else {
                violationSet = validator.validate(obj, groups);
            }
            if (!CollectionUtils.isEmpty(violationSet)) {
                resultMap.put(index, violationSet);
            }
            ++index;
        }
        if (process != null) {
            process.process(resultMap);
        }
    }

    /**
     * 校验第一个日期是否是第二个日期之前
     * startDate &lt; endDate
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    public static void isAfterDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (startDate.getTime() >= endDate.getTime()) {
            throw new RuntimeException("error.data.not.after");
        }
    }

    /**
     * 校验第一个日期是否是第二个日期之前或相等
     * startDate &lt;= endDate
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    public static void isSameOrAfterDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (startDate.getTime() > endDate.getTime()) {
            throw new RuntimeException("error.data.not.same.after");
        }
    }

    /**
     * 验证结果
     */
    public interface ValidationResult {
        /**
         * 批量验证结果处理接口，默认打印日志，抛出异常
         *
         * @param resultMap 验证结果
         * @param <T>       验证对象泛型
         */
        default <T> void process(Map<Integer, Set<ConstraintViolation<T>>> resultMap) {
            if (!CollectionUtils.isEmpty(resultMap)) {
                StringBuilder sb = new StringBuilder();
                resultMap.forEach((key, value) -> sb.append("index : ").append(key).append(" , error : ").append(
                        value.stream().map(item -> item.getPropertyPath().toString() + " " + item.getMessage())
                                .collect(Collectors.toList()).toString())
                        .append(";\n"));
                throw new RuntimeException(sb.toString());
            }
        }

        /**
         * 单个验证结果处理接口，默认打印日志，抛出异常
         *
         * @param resultSet 验证结果
         * @param <T>       验证对象泛型
         */
        default <T> void process(Set<ConstraintViolation<T>> resultSet) {
            if (!CollectionUtils.isEmpty(resultSet)) {
                throw new RuntimeException(resultSet
                        .stream()
                        .map(item -> item.getPropertyPath().toString() + " " + item.getMessage() + "; ")
                        .collect(Collectors.toList())
                        .toString());
            }
        }
    }
}