---
--- Created by wenpan
---
-- 由组 + 任务ID组成锁
local taskLock = ARGV[1];
-- 任务池
local taskPoolSet = ARGV[2];
-- 任务ID
local taskKey = ARGV[3];
-- 删除这个锁
redis.call('DEL', taskLock);
-- 将任务池中的这个任务得分减一（即尝试次数减一）
redis.call('ZINCRBY', taskPoolSet, -1, taskKey);