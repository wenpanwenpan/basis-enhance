package org.basis.enhance.exception.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.ArrayUtils;
import org.basis.enhance.exception.message.Message;
import org.basis.enhance.exception.message.MessageAccessor;

/**
 * 统一异常响应对象
 *
 * @author Mr_wenpan@163.com 2021/10/9 11:48 下午
 */
public class ExceptionResponse {

    public static final String FILED_FAILED = "failed";
    public static final String FILED_CODE = "code";
    public static final String FILED_MESSAGE = "message";
    public static final String FILED_TYPE = "type";

    private Boolean failed;
    private String code;
    private String message;
    private String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String exception;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String[] trace;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String[] throwable;

    public ExceptionResponse() {
        failed = true;
    }

    /**
     * 根据code自动获取描述信息、消息类型
     *
     * @param code 消息编码
     */
    public ExceptionResponse(String code) {
        failed = true;
        this.code = code;
        Message message = MessageAccessor.getMessage(code);
        this.message = message.desc();
        type = message.type();
    }

    public ExceptionResponse(Message message) {
        this(true, message);
    }

    public ExceptionResponse(boolean failed, Message message) {
        this(failed, message.code(), message.desc(), message.type());
    }

    public ExceptionResponse(String code, String message) {
        this(true, code, message, Message.DEFAULT_TYPE.code());
    }

    public ExceptionResponse(String code, String message, String type) {
        this(true, code, message, type);
    }

    public ExceptionResponse(boolean failed, String code, String message, String type) {
        this.failed = failed;
        this.code = code;
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public ExceptionResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getType() {
        return type;
    }

    public ExceptionResponse setType(String type) {
        this.type = type;
        return this;
    }

    public boolean getFailed() {
        return failed;
    }

    public ExceptionResponse setFailed(boolean failed) {
        this.failed = failed;
        return this;
    }

    public String getCode() {
        return code;
    }

    public ExceptionResponse setCode(String code) {
        this.code = code;
        return this;
    }

    public String getException() {
        return exception;
    }

    public ExceptionResponse setException(String exception) {
        this.exception = exception;
        return this;
    }

    public String[] getTrace() {
        return trace;
    }

    /**
     * 这个鬼字段会反序列化失败，忽略了
     */
    @JsonIgnore
    public ExceptionResponse setTrace(StackTraceElement[] trace) {
        this.trace = ArrayUtils.toStringArray(trace);
        return this;
    }

    public String[] getThrowable() {
        return throwable;
    }

    public ExceptionResponse setThrowable(String message, StackTraceElement[] trace) {
        throwable = ArrayUtils.insert(0, ArrayUtils.toStringArray(trace), message);
        return this;
    }
}