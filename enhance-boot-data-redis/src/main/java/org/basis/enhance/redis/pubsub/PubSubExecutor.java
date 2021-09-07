package org.basis.enhance.redis.pubsub;

/**
 * 发布订阅执行器
 *
 * @author Mr_wenpan@163.com 2021/08/31 14:07
 */
public interface PubSubExecutor {

    /**
     * 发布订阅执行方法
     *
     * @param message 消息
     * @author Mr_wenpan@163.com 2021/8/31 2:08 下午
     */
    void execute(String message);
}
