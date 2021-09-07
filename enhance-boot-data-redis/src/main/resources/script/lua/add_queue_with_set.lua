-- KEYS[1] : 队列 key
-- KEYS[2] : set key
-- ARGV[1] : dataJson
local queueKey = KEYS[1];
local setKey = KEYS[2];
local dataJsonList = cjson.decode(ARGV[1]);

for i, data in pairs(dataJsonList) do
    -- 队列插入数据
    redis.call('LPUSH', queueKey, data);
    -- 插入set表
    redis.call('SADD', setKey, data);
end