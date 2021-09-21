---
--- Created by wenpan
---

-- 按照子任务所属组 + 子任务ID 组成的锁信息
local taskLock = ARGV[1];
-- 当前节点信息
local node = ARGV[2]
-- 子任务过期时间
local expireSeconds = tonumber(ARGV[3]);
-- 任务池（按任务组组织，stone:executor:group:task_pool）
local taskPoolSet = ARGV[4];
-- 子任务ID
local taskKey = ARGV[5];
-- 在任务池中按照子任务id获取得分
local score = redis.call('ZSCORE', taskPoolSet, taskKey);
-- 得分为空则说明没有这个任务
if score == nil then
    return false;
end
-- 以任务组 + 任务ID拼接为taskLock，去设置值，值为节点信息。这里如果设置成功表示抢占任务成功
local setNXResult = redis.call('SETNX', taskLock, node);
if setNXResult == 0 then
    -- 任务抢占失败
    return false;
end
-- 任务抢占成功后设置该任务的过期时间
redis.call('EXPIRE', taskLock, expireSeconds);
-- 将任务池中该key的得分 +1
redis.call('ZINCRBY', taskPoolSet, 1, taskKey);
return true;