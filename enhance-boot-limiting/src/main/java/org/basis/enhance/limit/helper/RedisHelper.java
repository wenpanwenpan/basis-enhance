package org.basis.enhance.limit.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

/**
 * redis操作助手
 *
 * @author wenpanfeng 2022/07/04 21:25
 */
public class RedisHelper {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 将 Lua 脚本封装到 RedisScript 中执行
     *
     * @param redisScript Lua 脚本
     * @param keys        脚本中对应的key，可以用 KEYS[1]、KEYS[2]... 获取
     * @param args        脚本中用到的参数，可以用 ARGV[1]、ARGV[2]... 获取
     * @param <T>         返回类型
     * @return T
     */
    public <T> T executeScript(RedisScript<T> redisScript, List<String> keys, List<Object> args) {
        return redisTemplate.execute(redisScript, keys, args.toArray());
    }
}