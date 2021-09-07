-- KEYS[1] : 队列 key
-- KEYS[2] : set key
local queueKey = KEYS[1];
local queueData = redis.call('RPOP', queueKey);

if queueData then
    return queueData;
else
    return "";
end