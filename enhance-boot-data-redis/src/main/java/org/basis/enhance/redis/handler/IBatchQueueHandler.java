package org.basis.enhance.redis.handler;

import java.util.List;

/**
 * 队列数据批量处理器
 *
 * @author Mr_wenpan@163.com 2021/08/24 22:19
 */
public interface IBatchQueueHandler {

    /**
     * 每次消费的最大数量
     *
     * @return 最大数量
     */
    default int getSize() {
        return 500;
    }

    /**
     * 每次处理队列中的全部消息
     *
     * @param messages 从队列获取的消息内容
     */
    void handle(List<String> messages);

}
