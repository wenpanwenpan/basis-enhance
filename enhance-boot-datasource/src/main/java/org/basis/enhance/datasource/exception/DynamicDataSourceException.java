package org.basis.enhance.datasource.exception;

/**
 * 动态数据源异常
 *
 * @author Mr_wenpan@163.com 2022/04/07 18:04
 */
public class DynamicDataSourceException extends RuntimeException {

    public DynamicDataSourceException(String code) {
        super(code);
    }

    public DynamicDataSourceException(String code, Throwable cause) {
        super(code, cause);
    }

    public DynamicDataSourceException(Throwable cause) {
        super(cause);
    }

}
