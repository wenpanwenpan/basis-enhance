-- 令牌桶
local bucketKey = KEYS[1]
-- 上次请求的时间key
local last_request_time_key = 'lastRequestTime'
-- 令牌桶的容量
local capacity = tonumber(ARGV[1])
-- 请求令牌的数量
local permits = tonumber(ARGV[2])
-- 令牌流入的速率(按毫秒计算)
local rate = tonumber(ARGV[3])
-- 当前时间(毫秒)
local current_time = tonumber(ARGV[4])
-- 唯一标识
local unique_identifier = bucketKey

-- 恶意请求
if permits <= 0 then
    return 1
end

-- 获取当前桶内令牌的数量
local current_limit = tonumber(redis.call('HGET', unique_identifier, bucketKey) or '0')
-- 获取上次请求的时间
local last_mill_request_time = tonumber(redis.call('HGET', unique_identifier, last_request_time_key) or '0')
-- 计算向桶里添加令牌的数量
local add_token_num = 0
if last_mill_request_time == 0 then
	-- 如果是第一次请求，则进行初始化令牌桶，并且更新上次请求时间
	add_token_num = capacity
	redis.call("HSET", unique_identifier, last_request_time_key, current_time)
else
    -- 令牌流入桶内
	add_token_num = math.floor((current_time - last_mill_request_time) * rate)
end

-- 更新令牌的数量
if current_limit + add_token_num > capacity then
    current_limit = capacity
else
	current_limit = current_limit + add_token_num
end
-- 更新桶内令牌的数量
redis.pcall('HSET',unique_identifier, bucketKey, current_limit)

-- 限流判断
if current_limit - permits < 0 then
    -- 达到限流大小
    return 0
else
    -- 没有达到限流大小
	current_limit = current_limit - permits
	redis.pcall('HSET', unique_identifier, bucketKey, current_limit)
	-- 更新上次请求的时间
	redis.call('HSET', unique_identifier, last_request_time_key, current_time)
	return 1
end
