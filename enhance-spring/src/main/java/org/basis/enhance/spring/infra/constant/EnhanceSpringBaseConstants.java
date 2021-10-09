package org.basis.enhance.spring.infra.constant;

/**
 * 基础常量类
 *
 * @author Mr_wenpan@163.com 2021/10/09 15:12
 */
public interface EnhanceSpringBaseConstants {

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
