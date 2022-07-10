-- key对应着某个接口, value对应着这个接口的上一次请求时间
local unique_identifier = KEYS[1]
-- 上次请求时间key
local timeKey = 'lastTime'
-- 时间窗口内累计请求数量key
local requestKey = 'requestCount'
-- 限流大小,限流最大请求数
local maxRequest = tonumber(ARGV[1])
-- 当前请求时间戳,也就是请求的发起时间（毫秒）
local nowTime = tonumber(ARGV[2])
-- 窗口长度(毫秒)
local windowLength = tonumber(ARGV[3])

-- 限流开始时间
local currentTime = tonumber(redis.call('HGET', unique_identifier, timeKey) or '0')
-- 限流累计请求数
local currentRequest = tonumber(redis.call('HGET', unique_identifier, requestKey) or '0')

-- 当前时间在滑动窗口内
if currentTime + windowLength > nowTime then
    if currentRequest + 1 > maxRequest then
        return 0;
    else
        -- 在时间窗口内且请求数没超，请求数加一
        redis.call('HINCRBY', unique_identifier, requestKey, 1)
        return 1;
    end
else
    -- 超时后重置，开启一个新的时间窗口
    redis.call('HSET', unique_identifier, timeKey, nowTime)
    redis.call('HSET', unique_identifier, requestKey, '0')
    -- 窗口过期时间
    local expireTime = windowLength / 1000;
    redis.call('EXPIRE', unique_identifier, expireTime)
    redis.call('HINCRBY', unique_identifier, requestKey, 1)
    return 1;
end
