package org.clean.code.validation.controller;

import org.clean.code.validation.entity.UserInfo;
import org.clean.code.validation.util.ValidUtils;
import org.clean.code.validation.util.ValidationUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户信息controller
 * 经过多次测试，直接使用hibernate的校验注解 + @Validated或 @Valid进行校验是不稳定的，很多时候及时校验不通过都不会打印出异常信息
 * 而仅仅是输出日志，无法通过全局异常捕获，所以个人认为还是推荐{@link ValidUtils}来手动校验对象，在该类中如果校验不通过会自动抛出自定义
 * 校验异常，然后用全局异常捕获器将自定义校验异常捕获，然后统一返回错误信息给前端
 *
 * @author Mr_wenpan@163.com 2022/05/03 16:07
 */
@RestController
// 表示整个类都启用校验，如果遇到入参含有 bean validation注解的话就会自动校验
//@Validated
public class UserInfoController {

    /**
     * 使用javax.validation.constraints.NotNull;注解 + @Validated来校验入参
     */
    @GetMapping("/get-by-name")
    public String getByName(@NotNull(message = "name不能为空哟") String name) {

        return name + "-ok";
    }

    /**
     * 编程式校验(不推荐)
     */
    @GetMapping("/add-user")
    public String addUser(UserInfo userInfo) {

        // 手动调用工具类进行校验
        List<String> valid = ValidationUtil.valid(userInfo);
        // 如果有返回结果，则说明校验不成功
        if (valid != null) {
            System.out.println(valid);
            return "校验不成功";
        }

        return "校验成功";
    }

    /**
     * spring mvc环境中一般使用声明式校验，这种更简单，spring mvc也更推荐
     * 使用BindingResult来获取校验错误信息
     * 这里使用@Valid注解，注意和下面的@Validated的区别
     */
    @PostMapping("/add-user2")
    public String addUser2(@Valid @RequestBody UserInfo userInfo/*, BindingResult bindResult*/) {

        ValidUtils.valid(ValidationUtil.validator, userInfo);

        // 获取校验错误信息
//        if (bindResult.hasErrors()) {
//            List<ObjectError> allErrors = bindResult.getAllErrors();
//            for (ObjectError error : allErrors) {
//                System.out.println(error.getObjectName() + "::" + error.getDefaultMessage());
//            }
//            // 获取没通过校验的字段详情
//            List<FieldError> fieldErrors = bindResult.getFieldErrors();
//            for (FieldError fieldError : fieldErrors) {
//                System.out.println(fieldError.getField() + ":" + fieldError.getDefaultMessage()
//                        + "，当前没通过校验的值是：" + fieldError.getRejectedValue());
//            }
//            return "校验失败";
//        }

        return "校验成功";
    }

    /**
     * 使用@Validated注解来校验，该注解支持分组校验，@Valid注解不支持分组校验
     */
    @PostMapping("/add-user3")
    public String addUser3(@Validated(value = UserInfo.AddGroup.class) @RequestBody UserInfo userInfo/*, BindingResult bindResult*/) {

        // 获取校验错误信息
//        if (bindResult.hasErrors()) {
//            List<ObjectError> allErrors = bindResult.getAllErrors();
//            for (ObjectError error : allErrors) {
//                System.out.println(error.getObjectName() + "::" + error.getDefaultMessage());
//            }
//            // 获取没通过校验的字段详情
//            List<FieldError> fieldErrors = bindResult.getFieldErrors();
//            for (FieldError fieldError : fieldErrors) {
//                System.out.println(fieldError.getField() + ":" + fieldError.getDefaultMessage()
//                        + "，当前没通过校验的值是：" + fieldError.getRejectedValue());
//            }
//            return "校验失败";
//        }

        return "校验成功";
    }
}
