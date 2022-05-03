package org.basis.enhance.exception.base;

import org.basis.enhance.exception.handler.BaseExceptionHandler;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 约束校验异常
 *
 * @author Mr_wenpan@163.com 2022/05/03 18:07
 */
public class ConstraintValidateException extends Exception {

    /**
     * 暂存message中需要的parameters，在全局异常拦截器格式化异常消息的时候用
     * {@link BaseExceptionHandler#process(HttpServletRequest, HandlerMethod, CommonException)}
     */
    private final transient Object[] parameters;

    /**
     * 构造器
     *
     * @param message    异常信息
     * @param parameters parameters
     */
    public ConstraintValidateException(String message, Object... parameters) {
        super(message);
        this.parameters = parameters;
    }

    public ConstraintValidateException(Throwable cause, Object... parameters) {
        super(cause);
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
