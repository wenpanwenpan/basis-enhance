package org.basis.enhance.options;

import org.basis.enhance.helper.RedisHelper;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 操作redis db
 *
 * @author Mr_wenpan@163.com 2021/09/06 13:57
 */
public abstract class AbstractOptionsRedisDb<T, K> {

    RedisHelper redisHelper;

    public AbstractOptionsRedisDb(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    /**
     * 操作0号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbZero();


    /**
     * 操作一号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbOne();

    /**
     * 操作二号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbTwo();

    /**
     * 操作三号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbThree();

    /**
     * 操作四号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbFour();

    /**
     * 操作五号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbFive();

    /**
     * 操作六号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbSix();

    /**
     * 操作七号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbSeven();

    /**
     * 操作八号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbEight();

    /**
     * 操作九号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbNine();

    /**
     * 操作十号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbTen();

    /**
     * 操作十一号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbEleven();

    /**
     * 操作十二号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbTwelve();

    /**
     * 操作十三号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbThirteen();

    /**
     * 操作十四号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbFourteen();

    /**
     * 操作十五号db
     *
     * @return org.springframework.data.redis.core.RedisTemplate<T, K>
     */
    public abstract RedisTemplate<T, K> opsDbFifteen();


}
