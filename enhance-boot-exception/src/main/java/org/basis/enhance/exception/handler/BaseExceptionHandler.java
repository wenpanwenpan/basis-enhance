package org.basis.enhance.exception.handler;

import org.basis.enhance.exception.base.CheckedException;
import org.basis.enhance.exception.base.CommonException;
import org.basis.enhance.exception.base.ExceptionResponse;
import org.basis.enhance.exception.base.FeignException;
import org.basis.enhance.exception.infra.constant.EnhanceExceptionConstant;
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

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ExceptionResponse> processFeignException(HttpServletRequest request, HandlerMethod method, FeignException exception) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(exceptionMessage("Feign exception", request, method), exception);
        }
        ExceptionResponse er = new ExceptionResponse(MessageAccessor.getMessage(exception.getCode(), exception.getParameters()));
        setDevException(er, exception);
        return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ExceptionResponse> process(HttpServletRequest request, HandlerMethod method, CommonException exception) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(exceptionMessage("Common exception", request, method), exception);
        }
        ExceptionResponse er = new ExceptionResponse(MessageAccessor.getMessage(exception.getCode(), exception.getParameters()));
        setDevException(er, exception);
        return new ResponseEntity<>(er, HttpStatus.OK);
    }

    /**
     * 拦截 {@link RuntimeException} / {@link Exception} 异常信息，返回 “程序出现错误，请联系管理员” 信息
     *
     * @param exception 异常
     * @return ExceptionResponse
     */
    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<ExceptionResponse> process(HttpServletRequest request, Exception exception) {
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(exceptionMessage("Unknown exception", request, null), exception);
        }
        // 直接传入code到ExceptionResponse对象中，在该对象的构造方法里会去缓存或本地多语言文件查找对应的含义
        ExceptionResponse er = new ExceptionResponse(EnhanceExceptionConstant.ErrorCode.ERROR);
        setDevException(er, exception);
        return new ResponseEntity<>(er, HttpStatus.OK);
    }

    /**
     * 拦截 {@link CheckedException} 异常信息，返回 “程序出现错误，请联系管理员” 信息
     *
     * @param exception 异常
     * @return ExceptionResponse
     */
    @ExceptionHandler(CheckedException.class)
    public ResponseEntity<ExceptionResponse> process(HttpServletRequest request, HandlerMethod method, CheckedException exception) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(exceptionMessage("Checked exception", request, method), exception);
        }
        // 通过code获取到desc并格式化desc，然后封装为ExceptionResponse对象
        ExceptionResponse er = new ExceptionResponse(exception.getMessage(), exception.getParameters());
        setDevException(er, exception);
        return new ResponseEntity<>(er, HttpStatus.OK);
    }

    /**
     * 构建异常信息
     */
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
