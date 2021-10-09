package org.basis.enhance.redis.cache.l2;

/**
 * 缓存加载器
 *
 * @author Mr_wenpan@163.com 2021/10/8 5:26 下午
 */
@FunctionalInterface
public interface CacheLoader<K, V> {
    /**
     * 根据Key加载数据
     *
     * @param k 缓存Key
     * @return 值
     */
    V load(K k);
}