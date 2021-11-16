package org.enhance.data.redis.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.redis.delayqueue.RedisDelayQueue;
import org.enhance.data.redis.domain.entity.Order;
import org.springframework.stereotype.Service;

/**
 * 订单取消延时队列实现
 *
 * @author Mr_wenpan@163.com 2021/11/15 22:51
 */
@Slf4j
@Service
public class OrderCancelDelayQueueImpl extends RedisDelayQueue<Order> {

    @Override
    protected void doConsume(Order data) {
        log.info("收到消息，消息为：{}", data);
    }

    @Override
    public String queue() {
        return "order-cancel";
    }

}
