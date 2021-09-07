package org.basis.enhance.redis.infra.constant;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

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

    /**
     * redis script默认配置
     */
    interface RedisScript {
        /**
         * 生产队列默认脚本地址
         */
        String DEFAULT_PRODUCE_LUA_PATH = "script/lua/add_queue.lua";
        /**
         * 生产队列默认脚本地址（并操作set表）
         */
        String DEFAULT_PRODUCE_WITH_SET_LUA_PATH = "script/lua/add_queue_with_set.lua";
        /**
         * 消费队列默认脚本地址
         */
        String DEFAULT_CONSUMER_LUA_PATH = "script/lua/consume_queue.lua";
        /**
         * 消费队列默认脚本地址（并操作set表）
         */
        String DEFAULT_CONSUMER_WITH_SET_LUA_PATH = "script/lua/consume_queue_with_set.lua";

        /**
         * 通过传入的luaPath获取 DefaultRedisScript
         *
         * @param luaPath 脚本地址
         * @return DefaultRedisScript
         */
        static <T> DefaultRedisScript<T> getDefaultRedisScript(String luaPath) {
            ResourceScriptSource addQueueLua = new ResourceScriptSource(new ClassPathResource(luaPath));
            DefaultRedisScript<T> getRedisScript = new DefaultRedisScript<>();
            getRedisScript.setScriptSource(addQueueLua);
            return getRedisScript;
        }
    }

}
