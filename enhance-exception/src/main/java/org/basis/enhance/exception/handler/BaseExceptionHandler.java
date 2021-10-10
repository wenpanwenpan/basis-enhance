package org.basis.enhance.exception.handler;

import org.basis.enhance.exception.base.CommonException;
import org.basis.enhance.exception.base.ExceptionResponse;
import org.basis.enhance.exception.message.MessageAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.basis.enhance.exception.infra.constant.EnhanceExceptionConstant.DEFAULT_ENV;

/**
 * 异常拦截处理器
 *
 * @author Mr_wenpan@163.com 2021/10/09 23:43
 */
@ControllerAdvice
public class BaseExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseExceptionHandler.class);

    /**
     * 获取激活的环境配置
     */
    @Value("${spring.profiles.active:" + DEFAULT_ENV + "}")
    private String env;

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ExceptionResponse> process(HttpServletRequest request, HandlerMethod method, CommonException exception) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(exceptionMessage("Common exception", request, method), exception);
        }
        ExceptionResponse er = new ExceptionResponse(MessageAccessor.getMessage(exception.getCode(), exception.getParameters()));
        setDevException(er, exception);
        return new ResponseEntity<>(er, HttpStatus.OK);
    }

    private String exceptionMessage(String message, HttpServletRequest request, HandlerMethod method) {
        return String.format(message + ", Request: {URI=%s, method=%s}, User: %s", request.getRequestURI(),
                Optional.ofNullable(method).map(HandlerMethod::toString).orElse("NullMethod"), "null");
    }

    /**
     * 如果是Dev环境，则需要设置异常堆栈信息
     *
     * @param er 异常响应
     * @param ex 异常信息
     */
    private void setDevException(ExceptionResponse er, Exception ex) {
        // 在Dev环境下需要打印异常原因和异常堆栈
        if (DEFAULT_ENV.equals(env)) {
            // 设置异常消息
            er.setException(ex.getMessage());
            // 设置异常堆栈
            er.setTrace(ex.getStackTrace());

            Throwable cause = ex.getCause();
            // 如果有异常Throwable，那么还需要设置异常信息
            if (cause != null) {
                er.setThrowable(cause.getMessage(), cause.getStackTrace());
            }
        }
    }

}
