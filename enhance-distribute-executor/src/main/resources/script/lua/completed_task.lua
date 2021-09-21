---
--- created by wenpan
---
-- 主任务任务池
local taskProgress = ARGV[1];
-- 主任务ID
local mainTask = ARGV[2];
-- 任务锁
local taskLock = ARGV[3];
-- 任务池
local taskPoolSet = ARGV[4];
-- 任务ID
local taskKey = ARGV[5];
-- 将主任务池中的主任务的总任务数量减一
local result = tonumber(redis.call('ZINCRBY', taskProgress, -1, mainTask));
-- 如果减一后结果小于0，说明所有的子任务已经被执行完毕了，此时主任务就可以结束了，删除主任务
if result <= 0 then
    redis.call('ZREM', taskProgress, mainTask);
end
-- 从子任务池中删除子任务
redis.call('ZREM', taskPoolSet, taskKey);
-- 释放子任务锁
redis.call('DEL', taskLock);