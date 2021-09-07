package org.basis.enhance.redis.helper;

import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.redis.infra.constant.EnhanceRedisConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过lua脚本消费redis队列帮助器
 *
 * @author Mr_wenpan@163.com 2021/09/07 20:58
 */
public class QueueConsumerByLuaHelper {

    /**
     * 消费队列
     *
     * @param queueKey      队列 key
     * @param redisTemplate redisTemplate
     * @return dataJson
     */
    public String consumeQueue(String queueKey, RedisTemplate<String, String> redisTemplate) {

        return consumeQueue(queueKey, null, null, redisTemplate);
    }

    /**
     * 消费队列
     *
     * @param queueKey      队列 key
     * @param luaPath       lua脚本相对地址
     * @param redisTemplate redisTemplate
     * @return dataJson
     */
    public String consumeQueue(String queueKey, String luaPath, RedisTemplate<String, String> redisTemplate) {

        return consumeQueue(queueKey, null, luaPath, redisTemplate);
    }

    /**
     * 消费队列
     *
     * @param queueKey      队列 key
     * @param setKey        set key
     * @param luaPath       lua脚本相对地址
     * @param redisTemplate redisTemplate
     * @return dataJson
     */
    public String consumeQueue(String queueKey, String setKey, String luaPath, RedisTemplate<String, String> redisTemplate) {

        if (StringUtils.isBlank(luaPath)) {
            if (null == setKey) {
                luaPath = EnhanceRedisConstants.RedisScript.DEFAULT_CONSUMER_LUA_PATH;
            } else {
                luaPath = EnhanceRedisConstants.RedisScript.DEFAULT_CONSUMER_WITH_SET_LUA_PATH;
            }
        }

        DefaultRedisScript<String> defaultRedisScript = EnhanceRedisConstants.RedisScript.getDefaultRedisScript(luaPath);
        defaultRedisScript.setResultType(String.class);
        List<String> keys = new ArrayList<>();
        keys.add(queueKey);
        if (null != setKey) {
            keys.add(setKey);
        }

        return (String) redisTemplate.execute(defaultRedisScript, keys);
    }
}
