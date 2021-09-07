-- KEYS[1] : 队列 key
-- ARGV[1] : dataJsonList
local queueKey = KEYS[1];
local dataJsonList = cjson.decode(ARGV[1]);

for i, data in pairs(dataJsonList) do
    -- 队列插入数据
    redis.call('LPUSH', queueKey, data);
end