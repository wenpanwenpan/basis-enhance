package org.basis.enhance.limit.helper;

import com.google.common.collect.Lists;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 限流帮助器
 *
 * @author wenpanfeng 2022/7/4 21:22
 */
public class RedisLimitHelper {

    /**
     * redis-key: 滑动时间窗开始时间key
     */
    private static final String LIMIT_TIME_PREFIX = "limit:slide-window:";
    /**
     * 令牌桶限流key前缀
     */
    private static final String BUCKET_LIMIT_KEY_PREFIX = "limit:token-bucket:";
    /**
     * 滑动时间窗限流脚本
     */
    private static final String SLIDING_WINDOW_LIMIT_SCRIPT_LUA = "script/lua/sliding-window-limit.lua";
    /**
     * 令牌桶限流算法脚本
     */
    private static final String TOKEN_BUCKET_LIMIT_SCRIPT_LUA = "script/lua/token-bucket-flow-limiting.lua";
    /**
     * 滑动时间窗限流
     */
    private static final DefaultRedisScript<Boolean> SLIDING_WINDOW_LIMIT_SCRIPT;
    /**
     * 令牌桶限流
     */
    private static final DefaultRedisScript<Boolean> TOKEN_BUCKET_LIMIT_SCRIPT;

    static {
        SLIDING_WINDOW_LIMIT_SCRIPT = new DefaultRedisScript<>();
        SLIDING_WINDOW_LIMIT_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(SLIDING_WINDOW_LIMIT_SCRIPT_LUA)));
        SLIDING_WINDOW_LIMIT_SCRIPT.setResultType(Boolean.class);

        TOKEN_BUCKET_LIMIT_SCRIPT = new DefaultRedisScript<>();
        TOKEN_BUCKET_LIMIT_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource(TOKEN_BUCKET_LIMIT_SCRIPT_LUA)));
        TOKEN_BUCKET_LIMIT_SCRIPT.setResultType(Boolean.class);
    }

    RedisHelper redisHelper;

    public RedisLimitHelper(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    /**
     * 令牌桶限流（支持毫秒级限流）
     *
     * @param limitKey 限流key
     * @param capacity 桶容量
     * @param permits  申请令牌数量
     * @param rate     令牌流入速率
     * @return java.lang.Boolean false 表示被限流，true表示没被限流
     * @author wenpan 2022/7/5 9:39 下午
     */
    public Boolean tokenLimit(String limitKey, int capacity, int permits, double rate) {
        // 令牌桶名称
        List<String> keys = Lists.newArrayListWithCapacity(1);
        String bucket = BUCKET_LIMIT_KEY_PREFIX + limitKey;
        keys.add(bucket);
        List<Object> args = new ArrayList<>();
        args.add(String.valueOf(capacity));
        args.add(String.valueOf(permits));
        // 将秒换算为毫秒（如果每秒流入1个令牌，则这里每毫秒流入0.001个令牌）
        double rateNum = rate / 1000;
        args.add(String.valueOf(rateNum));
        // 毫秒
        long currentTime = System.currentTimeMillis();
        args.add(String.valueOf(currentTime));
        return redisHelper.executeScript(TOKEN_BUCKET_LIMIT_SCRIPT, keys, args);
    }

    /**
     * 滑动时间窗限流
     *
     * @param limitKey     限流key
     * @param maxRequest   一个时间窗口内最大请求次数
     * @param windowLength 时间窗口大小(单位：秒)
     * @return java.lang.Boolean false 表示被限流，true表示没被限流
     * @author wenpanfeng 2022/7/4 21:22
     */
    public Boolean windowLimit(String limitKey, int maxRequest, int windowLength) {
        // 获取key名，一个时间窗口开始时间(限流开始时间)和一个时间窗口内请求的数量累计(限流累计请求数)
        List<String> keys = new ArrayList<>();
        keys.add(LIMIT_TIME_PREFIX + limitKey);
        // 传入参数，限流最大请求数，当前时间戳，一个时间窗口时间(毫秒)(限流时间)
        List<Object> args = new ArrayList<>();
        args.add(String.valueOf(maxRequest));
        args.add(String.valueOf(System.currentTimeMillis()));
        // 将秒转换为毫秒
        windowLength = windowLength * 1000;
        args.add(String.valueOf(windowLength));
        return redisHelper.executeScript(SLIDING_WINDOW_LIMIT_SCRIPT, keys, args);
    }

}
