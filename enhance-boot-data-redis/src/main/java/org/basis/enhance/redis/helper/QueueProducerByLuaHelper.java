package org.basis.enhance.redis.helper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.basis.enhance.redis.infra.bo.QueueBaseEntity;
import org.basis.enhance.redis.infra.constant.EnhanceRedisConstants;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过lua脚本向redis队列批量生成消息帮助器
 *
 * @author Mr_wenpan@163.com 2021/09/07 20:59
 */
public class QueueProducerByLuaHelper {

    /**
     * 加入队列
     *
     * @param queueKey      队列 key
     * @param redisTemplate redisTemplate
     * @param data          需要加入队列的数据
     */
    public void addQueue(String queueKey,
                         RedisTemplate<String, String> redisTemplate,
                         List<? extends QueueBaseEntity> data) {

        addQueue(queueKey, null, null, redisTemplate, data);
    }

    /**
     * 加入队列
     *
     * @param queueKey      队列 key
     * @param luaPath       lua脚本相对地址
     * @param redisTemplate redisTemplate
     * @param data          需要加入队列的数据
     */
    public void addQueue(String queueKey, String luaPath,
                         RedisTemplate<String, String> redisTemplate,
                         List<? extends QueueBaseEntity> data) {

        addQueue(queueKey, null, luaPath, redisTemplate, data);
    }

    /**
     * 加入队列
     *
     * @param queueKey      队列 key
     * @param setKey        set key
     * @param luaPath       lua脚本相对地址
     * @param redisTemplate redisTemplate
     * @param data          需要加入队列的数据
     */
    public void addQueue(String queueKey, String setKey, String luaPath,
                         RedisTemplate<String, String> redisTemplate, List<? extends QueueBaseEntity> data) {

        if (StringUtils.isBlank(luaPath)) {
            if (null == setKey) {
                luaPath = EnhanceRedisConstants.RedisScript.DEFAULT_PRODUCE_LUA_PATH;
            } else {
                luaPath = EnhanceRedisConstants.RedisScript.DEFAULT_PRODUCE_WITH_SET_LUA_PATH;
            }
        }

        List<String> keys = new ArrayList<>();
        keys.add(queueKey);
        if (null != setKey) {
            keys.add(setKey);
        }
        String[] args = new String[1];
        List<String> dataJsonList = new ArrayList<>();
        for (QueueBaseEntity datum : data) {
            dataJsonList.add(JSONObject.toJSONString(datum));
        }
        args[0] = JSONArray.toJSONString(dataJsonList);
        redisTemplate.execute(EnhanceRedisConstants.RedisScript.getDefaultRedisScript(luaPath), keys, args);
    }
}
