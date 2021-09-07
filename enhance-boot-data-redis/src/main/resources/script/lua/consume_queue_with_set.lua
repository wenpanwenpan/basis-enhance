-- KEYS[1] : 队列 key
-- KEYS[2] : set key
local queueKey = KEYS[1];
local queueDistinctSetKey = KEYS[2];
local queueData = redis.call('RPOP', queueKey);

if queueData then
    -- 删除set表
    redis.call('SREM', queueDistinctSetKey, queueData);
    return queueData;
else
    return "";
end