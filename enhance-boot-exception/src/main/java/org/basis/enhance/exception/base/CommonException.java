package org.basis.enhance.exception.base;

import org.basis.enhance.exception.handler.BaseExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 封装运行异常为CommonException
 *
 * @author Mr_wenpan@163.com 2021/10/9 3:10 下午
 */
public class CommonException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(CommonException.class);

    private static final long serialVersionUID = 5044938065901970022L;

    /**
     * 暂存message中需要的parameters，在全局异常拦截器格式化异常消息的时候用
     * {@link BaseExceptionHandler#process(HttpServletRequest, HandlerMethod, CommonException)}
     */
    private final transient Object[] parameters;

    /**
     * 暂存code
     */
    private String code;

    /**
     * 构造器
     *
     * @param code       异常code
     * @param parameters parameters
     */
    public CommonException(String code, Object... parameters) {
        super(code);
        this.parameters = parameters;
        this.code = code;
    }

    public CommonException(String code, Throwable cause, Object... parameters) {
        super(code, cause);
        this.parameters = parameters;
        this.code = code;
    }

    public CommonException(String code, Throwable cause) {
        super(code, cause);
        this.code = code;
        parameters = new Object[]{};
    }

    public CommonException(Throwable cause, Object... parameters) {
        super(cause);
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public String getCode() {
        return code;
    }

    /**
     * 获取异常堆栈
     *
     * @return java.lang.String
     */
    public String getTrace() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = null;
        try {
            ps = new PrintStream(baos, false, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            logger.error("Error get trace, unsupported encoding.", e);
            return null;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        printStackTrace(ps);
        ps.flush();
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("code", code);
        map.put("message", super.getMessage());
        return map;
    }

}
