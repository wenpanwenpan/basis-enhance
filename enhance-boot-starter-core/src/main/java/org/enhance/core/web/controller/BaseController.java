package org.enhance.core.web.controller;

import org.enhance.core.web.utils.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.validation.Validator;
import java.util.Collection;

/**
 * <p>
 * 基础Controller，提供一些controller层基础的通用能力
 * </p>
 *
 * @author Mr_wenpan@163.com 2022/4/15 5:22 下午
 */
public class BaseController {

    @Autowired
    private Validator validator;

    /**
     * 控制层请求String字符串去除前后空格 主要用于get搜索接口请求参数 post/put请求对象里面的String字段无法操作
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor stringTrimmer = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringTrimmer);
    }

    /**
     * 验证单个对象
     *
     * @param object 验证对象
     * @param groups 验证分组
     * @param <T>    Bean 泛型
     */
    protected <T> void validObject(T object, @SuppressWarnings("rawtypes") Class... groups) {
        ValidUtils.valid(validator, object, groups);
    }

    /**
     * 验证单个对象
     *
     * @param object  验证对象
     * @param groups  验证分组
     * @param process 异常信息处理
     * @param <T>     Bean 泛型
     */
    protected <T> void validObject(T object, ValidUtils.ValidationResult process, @SuppressWarnings("rawtypes") Class... groups) {
        ValidUtils.valid(validator, object, process, groups);
    }

    /**
     * 迭代验证集合元素，使用默认异常信息处理
     *
     * @param collection Bean集合
     * @param groups     验证组
     * @param <T>        Bean 泛型
     */
    protected <T> void validList(Collection<T> collection, @SuppressWarnings("rawtypes") Class... groups) {
        ValidUtils.valid(validator, collection, groups);
    }

    /**
     * 迭代验证集合元素，使用默认异常信息处理
     *
     * @param collection Bean集合
     * @param groups     验证组
     * @param process    异常信息处理
     * @param <T>        Bean 泛型
     */
    protected <T> void validList(Collection<T> collection, ValidUtils.ValidationResult process, @SuppressWarnings("rawtypes") Class... groups) {
        ValidUtils.valid(validator, collection, process, groups);
    }

}