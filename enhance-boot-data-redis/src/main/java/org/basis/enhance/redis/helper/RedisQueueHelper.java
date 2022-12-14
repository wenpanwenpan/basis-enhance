package org.basis.enhance.redis.helper;


import org.basis.enhance.redis.config.properties.StoneRedisProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * redis队列操作帮助器，基于Redis的消息队列，生产者消费者模式
 * todo redis官方要求集群模式不支持库的切换，如果生产上的Redis部署模式是集群，这里指定db然后去拉取数据会有问题
 * 所以这里改用连接Redis时使用的默认db
 *
 * @author Mr_wenpan@163.com 2021/08/10 22:45
 */
public class RedisQueueHelper {

    /**
     * 默认数据源的redisHelper（非动态多数据源）
     */
    private final RedisHelper redisHelper;

    private final StoneRedisProperties properties;

    public RedisQueueHelper(RedisHelper redisHelper, StoneRedisProperties redisProperties) {
        this.redisHelper = redisHelper;
        properties = redisProperties;
    }

    /**
     * 添加消息到消息队列
     *
     * @param isFromLeft 是否从左边推入队列
     * @param queue      队列key
     * @param value      队列消息
     */
    public Long push(boolean isFromLeft, String queue, String value) {
        return EasyRedisHelper.executeWithResult(properties.getQueueDb(), () -> {
            if (isFromLeft) {
                return redisHelper.lstLeftPush(queue, value);
            }
            return redisHelper.lstRightPush(queue, value);
        });
    }

    /**
     * 往指定的redis db 的队列里推送数据
     *
     * @param db         redis db
     * @param isFromLeft 是否从左边推入队列
     * @param queue      队列key
     * @param value      消息
     */
    public Long push(int db, boolean isFromLeft, String queue, String value) {
        return EasyRedisHelper.executeWithResult(db, () -> {
            if (isFromLeft) {
                return redisHelper.lstLeftPush(queue, value);
            }
            return redisHelper.lstRightPush(queue, value);
        });
    }

    /**
     * 往redis队列里批量推送数据
     *
     * @param isFromLeft 是否从左边推入队列
     * @param queue      队列key
     * @param collection 消息集合
     */
    public Long pushAll(boolean isFromLeft, String queue, Collection<String> collection) {
        return EasyRedisHelper.executeWithResult(properties.getQueueDb(), () -> {
            if (isFromLeft) {
                return redisHelper.lstLeftPushAll(queue, collection);
            }
            return redisHelper.lstRightPushAll(queue, collection);
        });
    }

    /**
     * 往指定的redis db 的队列里批量推送数据
     *
     * @param db         redis db
     * @param isFromLeft 是否从左边推入队列
     * @param queue      队列key
     * @param collection 消息集合
     */
    public Long pushAll(int db, boolean isFromLeft, String queue, Collection<String> collection) {
        return EasyRedisHelper.executeWithResult(db, () -> {
            if (isFromLeft) {
                return redisHelper.lstLeftPushAll(queue, collection);
            }
            return redisHelper.lstRightPushAll(queue, collection);
        });
    }

    /**
     * 指定方方向，从指定队列拉取数据，每次拉取一条
     *
     * @param isFromLeft 是否从队列左边拉
     * @param queue      队列名称
     */
    public String pop(boolean isFromLeft, String queue) {
        return EasyRedisHelper.executeWithResult(properties.getQueueDb(), () -> {
            if (isFromLeft) {
                return redisHelper.lstLeftPop(queue);
            }
            return redisHelper.lstRightPop(queue);
        });
    }

    /**
     * 指定方方向，从指定队列批量拉取数据
     *
     * @param isFromLeft 是否从队列左边拉
     * @param queue      队列名称
     * @param max        每次最多拉多少条
     */
    public List<String> popAll(boolean isFromLeft, String queue, int max) {
        return popAllCommon(isFromLeft, properties.getQueueDb(), queue, max);
    }

    /**
     * 指定方方向，指定db，从指定队列批量拉取数据
     *
     * @param isFromLeft 是否从队列左边拉
     * @param db         redis db
     * @param queue      队列名称
     * @param max        每次最多拉多少条
     */
    public List<String> popAll(boolean isFromLeft, int db, String queue, int max) {
        return popAllCommon(isFromLeft, db, queue, max);
    }

    /**
     * 批量获取队列消息(默认Redis db)
     *
     * @param key key
     */
    public List<String> popAll(String key) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        List<String> result = new ArrayList<>();
        while (true) {
            String str = pop(true, key);
            if (str == null) {
                break;
            }
            result.add(str);
        }
        redisHelper.clearCurrentDatabase();
        return result;
    }

    /**
     * popAll公用方法
     */
    private List<String> popAllCommon(boolean isFromLeft, int db, String queue, int max) {
        return EasyRedisHelper.executeWithResult(db, () -> {
            String str;
            List<String> result = new ArrayList<>();
            while (result.size() < max) {
                if (isFromLeft) {
                    str = redisHelper.lstLeftPop(queue);
                } else {
                    str = redisHelper.lstRightPop(queue);
                }
                if (str == null) {
                    break;
                }
                result.add(str);
            }
            return result;
        });
    }

}
