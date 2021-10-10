package org.basis.enhance.exception.infra.constant;

/**
 * 增强异常常量
 *
 * @author Mr_wenpan@163.com 2021/10/10 11:15
 */
public interface EnhanceExceptionConstant {

    /**
     * 默认环境
     */
    String DEFAULT_ENV = "dev";

    interface ErrorCode {
        /**
         * 响应超时
         */
        String TIMEOUT = "error.timeout";
        /**
         * 程序出现错误，请联系管理员
         */
        String ERROR = "error.error";
    }

}
