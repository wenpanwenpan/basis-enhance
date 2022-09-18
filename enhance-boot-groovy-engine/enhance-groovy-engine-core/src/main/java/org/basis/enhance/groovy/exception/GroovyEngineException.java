package org.basis.enhance.groovy.exception;

/**
 * groovy脚本引擎异常
 *
 * @author wenpan 2022/09/18 13:35
 */
public class GroovyEngineException extends RuntimeException {

    public GroovyEngineException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public GroovyEngineException(String message) {
        super(message);
    }

}
