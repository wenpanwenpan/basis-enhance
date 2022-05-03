package org.clean.code.validation.service;

import org.clean.code.validation.util.ValidationUtil;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 用户信息service
 *
 * @author Mr_wenpan@163.com 2022/05/03 15:54
 */
public class UserInfoService {

    /**
     * 在方法入参前加注解
     * 执行入参校验
     * 这种方式，一般需要结合aop使用，这种直接使用太麻烦了。如果是web环境那就不用自己实现了，spring-web已经帮我们实现好了
     */
    public String getByName(@NotNull String name) {
        StackTraceElement st = Thread.currentThread().getStackTrace()[1];
        String methodName = st.getMethodName();
        Method method = null;
        try {
            method = getClass().getDeclaredMethod(methodName, String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 入参校验
        List<String> result = ValidationUtil.validNotBean(this, method, new Object[]{name});
        System.out.println(result);

        return "ok";
    }
}
