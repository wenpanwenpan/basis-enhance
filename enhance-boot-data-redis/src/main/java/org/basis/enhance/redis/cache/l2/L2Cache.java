package org.basis.enhance.redis.cache.l2;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import org.basis.enhance.redis.helper.EasyRedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 二级缓存
 *
 * @author Mr_wenpan@163.com 2021/10/8 5:27 下午
 */
public class L2Cache<K, V> {

    private final Logger logger = LoggerFactory.getLogger(L2Cache.class);

    /**
     * 缓存初始容量
     */
    private static final int CACHE_INITIAL_CAPACITY = 1024;
    /**
     * 缓存最大容量
     */
    private static final int CACHE_MAXIMUM_SIZE = 1 << 17;
    /**
     * 缓存并发级别
     */
    private static final int CACHE_CONCURRENCY_LEVEL = 16;

    private final V PLACEHOLDER = (V) new Object();

    /**
     * 缓存
     */
    private final Cache<K, V> cache;
    /**
     * 缓存加载器
     */
    private final CacheLoader<K, V> loader;

    /**
     * 构建二级缓存器，缓存将永不过期
     *
     * @param loader 当缓存中Key对应的Value不存在时，使用这个缓存加载器来加载数据
     */
    public L2Cache(CacheLoader<K, V> loader) {
        this(loader, CACHE_INITIAL_CAPACITY, CACHE_MAXIMUM_SIZE, CACHE_CONCURRENCY_LEVEL, -1);
    }

    /**
     * 构建二级缓存器
     *
     * @param loader       当缓存中Key对应的Value不存在时，使用这个缓存加载器来加载数据
     * @param timeoutInSec 缓存Key过期秒数，写入缓存超过这个时间后将自动失效这个Key. 设置为 -1 时，将永不过期.
     *                     失效后再次获取时将使用 loader 重新加载数据到缓存
     */
    public L2Cache(CacheLoader<K, V> loader, long timeoutInSec) {
        this(loader, CACHE_INITIAL_CAPACITY, CACHE_MAXIMUM_SIZE, CACHE_CONCURRENCY_LEVEL, timeoutInSec);
    }

    /**
     * 构建二级缓存器
     *
     * @param loader           当缓存中Key对应的Value不存在时，使用这个缓存加载器来加载数据
     * @param initialCapacity  缓存初始容量
     * @param maximumSize      缓存最大容量
     * @param concurrencyLevel 缓存并发修改度
     * @param timeoutInSec     缓存Key过期秒数，写入缓存超过这个时间后将自动失效这个Key. 设置为 -1 时，将永不过期.
     *                         失效后再次获取时将使用 loader 重新加载数据到缓存
     */
    public L2Cache(CacheLoader<K, V> loader, int initialCapacity, int maximumSize, int concurrencyLevel, long timeoutInSec) {
        Assert.notNull(loader, "CacheLoader must not null.");
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .concurrencyLevel(concurrencyLevel)
                // 缓存中的value值都包裹在一个SoftReference(软引用)对象中，可以在内存过低的时候被当作垃圾回收。
                .softValues()
                .recordStats();

        if (timeoutInSec > 0) {
            builder.expireAfterWrite(timeoutInSec, TimeUnit.SECONDS);
        }

        this.loader = loader;
        // 构建缓存
        cache = builder.build();
    }

    /**
     * 根据Key获取缓存中的值，不存在将返回 null
     *
     * @param k key
     * @return 不存在将返回 null
     */
    @Nullable
    public V get(K k) {
        V v = cache.getIfPresent(k);
        if (v == null) {
            // 该key无效
            cache.invalidate(k);
            try {
                // get 会保证 loader 只被执行一次（保证了二级缓存不会被击穿而造成雪崩效应）
                // 当get方法从二级缓存没有获取到值时，才调用后面的callable方法去数据源获取值
                v = cache.get(k, () -> {
                    // 通过缓存加载器重新去数据源加载一下数据
                    V t = loader.load(k);

                    if (t == null) {
                        t = PLACEHOLDER;
                    }
                    return t;
                });
            } catch (Exception e) {
                logger.error("query [" + k + "] from cache error.", e);
            }
        }

        if (v == null || Objects.equals(v, PLACEHOLDER)) {
            return null;
        }
        return v;
    }

    /**
     * 输出缓存状态
     */
    public CacheStats stats() {
        return cache.stats();
    }

    /**
     * 返回一个获取 hash 中值的 CacheLoader
     *
     * @param separator hash key 的分隔符
     * @param redisdb   redis db
     * @return CacheLoader
     */
    public static CacheLoader<String, String> getHashValueCacheLoader(String separator, int redisdb) {
        return key -> {
            String[] arr = StringUtils.tokenizeToStringArray(key, separator);
            return EasyRedisHelper.execute(redisdb, (helper) -> {
                return helper.hshGet(arr[0], arr[1]);
            });
        };
    }

    /**
     * 返回一个获取 hash 中所有值的 CacheLoader
     *
     * @param redisdb redis db
     * @return CacheLoader
     */
    public static CacheLoader<String, Map<String, String>> getHashAllCacheLoader(int redisdb) {
        return key -> {
            return EasyRedisHelper.execute(redisdb, (helper) -> {
                return helper.hshGetAll(key);
            });
        };
    }

}