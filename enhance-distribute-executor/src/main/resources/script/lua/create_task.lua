---
--- Created by wenpan
---
-- 将map字符串解码为数组（形如：[(子任务ID,子任务所属组)，((子任务ID,子任务所属组))]）
local tasksWithGroup = cjson.decode(ARGV[1]);
-- 任务池（按任务组组织）
local taskPoolSetKey = ARGV[2];
-- stone:executor:task_progress
local mainTaskProgress = ARGV[3];
-- 主任务ID
local mainTask = ARGV[4]
-- 子任务总数
local taskCount = tonumber(ARGV[5]);
-- 遍历每个子任务
for task, group in pairs(tasksWithGroup) do
    -- 将group信息格式化到taskPoolSetKey中（stone:executor:%s:task_pool）
    local taskPoolSet = string.format(taskPoolSetKey, group);
    -- 将子任务添加到任务池，且添加的时候每个子任务的得分都为0
    redis.call('ZADD', taskPoolSet, 0, task);
end
-- 添加主任务，得分为任务总数taskCount，value为mainTask（即主任务ID）
redis.call('ZADD', mainTaskProgress, taskCount, mainTask);
