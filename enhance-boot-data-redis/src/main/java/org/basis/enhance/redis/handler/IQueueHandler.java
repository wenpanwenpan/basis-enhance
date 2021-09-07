package org.basis.enhance.redis.handler;

/**
 * 队列数据逐条处理器
 *
 * @author Mr_wenpan@163.com 2021/08/24 22:20
 */
public interface IQueueHandler {

    /**
     * 每次处理队列中的第一条消息
     *
     * @param message 消息内容
     */
    void handle(String message);

}
