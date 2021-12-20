package org.basis.enhance.mongo.exception;

/**
 * 分片算法已存在异常
 *
 * @author Mr_wenpan@163.com 2021/12/19 23:10
 */
public class ShardingAlgorithmExistedException extends RuntimeException {

    public ShardingAlgorithmExistedException(String code) {
        super(code);
    }

    public ShardingAlgorithmExistedException(String code, Throwable cause) {
        super(code, cause);
    }

    public ShardingAlgorithmExistedException(Throwable cause) {
        super(cause);
    }

}
