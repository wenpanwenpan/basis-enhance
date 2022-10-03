package org.basis.enhance.groovy.exception;

/**
 * 注册脚本异常
 *
 * @author wenpan 2022/10/03 12:03
 */
public class RegisterScriptException extends RuntimeException {

    public RegisterScriptException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RegisterScriptException(String message) {
        super(message);
    }
}
