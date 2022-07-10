package org.basis.enhance.limit.exception;

/**
 * redis限流异常
 *
 * @author wenpanfeng 2021/07/14 21:09
 */
public class EnhanceRedisLimitException extends RuntimeException {

    public EnhanceRedisLimitException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public EnhanceRedisLimitException(String message) {
        super(message);
    }

}