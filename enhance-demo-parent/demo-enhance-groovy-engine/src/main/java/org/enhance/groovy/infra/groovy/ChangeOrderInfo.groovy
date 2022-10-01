package org.enhance.groovy.infra.groovy

import org.enhance.groovy.api.dto.OrderInfoDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ChangeOrderInfo extends Script {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    Object run() {
        // 调用方法
        changeOrderInfo();
    }

    // 修改订单信息
    OrderInfoDTO changeOrderInfo() {
        String newOrderAmount = null;
        // 获取参数
        OrderInfoDTO orderInfoDTO = orderInfo;

        // 模拟一大堆的判断逻辑
        if (orderInfoDTO.getOrderId() < 10) {
            newOrderAmount = "2000";
        } else if (orderInfoDTO.getOrderId() < 20 && orderInfoDTO.getOrderId() >= 10) {
            newOrderAmount = "5000";
        } else if (orderInfoDTO.getOrderId() < 30 && orderInfoDTO.getOrderId() >= 20) {
            newOrderAmount = "8000";
        } else {
            newOrderAmount = "10000";
        }

        logger.info("即将修改订单金额，原金额为：{}, 修改后的金额为：{}", orderInfoDTO.getOrderAmount(), newOrderAmount);
        orderInfoDTO.setOrderAmount(newOrderAmount);
        // 返回修改后的结果
        return orderInfoDTO;
    }
}
