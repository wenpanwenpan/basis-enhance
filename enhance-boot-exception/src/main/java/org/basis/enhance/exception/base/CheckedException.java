package org.basis.enhance.exception.base;

/**
 * 受检查异常
 *
 * @author Mr_wenpan@163.com 2021/10/10 10:56 下午
 */
public class CheckedException extends Exception {

    private static final long serialVersionUID = 7638819698564041064L;

    private final transient Object[] parameters;

    /**
     * 构造器
     *
     * @param message    异常信息
     * @param parameters parameters
     */
    public CheckedException(String message, Object... parameters) {
        super(message);
        this.parameters = parameters;
    }

    public CheckedException(Throwable cause, Object... parameters) {
        super(cause);
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }
}