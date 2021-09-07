package org.basis.enhance.infra.constant;

/**
 * data-redis增强模块常用常量
 *
 * @author Mr_wenpan@163.com 2021/08/25 15:57
 */
public interface EnhanceRedisConstants {
    /**
     * 常用数字
     */
    interface Digital {
        int NEGATIVE_ONE = -1;
        int ZERO = 0;
        int ONE = 1;
        int TWO = 2;
        int FOUR = 4;
        int FIVE = 5;
        int EIGHT = 8;
        int TEN = 10;
        int SIXTEEN = 16;
    }

    /**
     * 多数据源相关常量
     */
    interface MultiSource {

        String REDIS_TEMPLATE = "RedisTemplate";

        String REDIS_HELPER = "RedisHelper";

        String DEFAULT_SOURCE = "defaultSource";

        String DEFAULT_SOURCE_HELPER = "defaultSourceRedisHelper";

        String DEFAULT_SOURCE_TEMPLATE = "defaultSourceRedisTemplate";
    }

    /**
     * 默认redis数据源的redisHelper注入名称
     */
    interface DefaultRedisHelperName {

        String REDIS_HELPER = "redisHelper";

        String DEFAULT = "default";

        String DEFAULT_REDIS_HELPER = "default-helper";
    }

    /**
     * 默认redis数据源的redisHelper注入名称
     */
    interface DefaultRedisTemplateName {

        String REDIS_TEMPLATE = "redisTemplate";

    }

}
