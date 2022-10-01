package org.enhance.groovy.api.dto;

import lombok.Data;

/**
 * 订单信息DTO
 *
 * @author wenpan 2022/09/30 21:58
 */
@Data
public class OrderInfoDTO {

    private Integer orderId;

    private String orderName;

    private String orderNumber;

    private String orderAmount;
}
