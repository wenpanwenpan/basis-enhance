package org.enhance.data.redis.domain.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 模拟订单数据
 *
 * @author Mr_wenpan@163.com 2021/11/16 10:37 上午
 */
@Data
public class Order implements Serializable {

    private String orderId;

    private String orderNumber;

    private String orderAmount;
}