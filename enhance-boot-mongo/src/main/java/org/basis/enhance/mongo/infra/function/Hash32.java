package org.basis.enhance.mongo.infra.function;

/**
 * Hash计算接口
 *
 * @param <T> 被计算hash的对象类型
 * @since 5.2.5
 */
@FunctionalInterface
public interface Hash32<T> {
    /**
     * 计算Hash值
     *
     * @param t 对象
     * @return hash
     */
    int hash32(T t);
}