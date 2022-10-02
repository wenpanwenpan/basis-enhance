package org.enhance.groovy.api.controller;

import org.basis.enhance.groovy.entity.ExecuteParams;
import org.enhance.groovy.api.dto.OrderInfoDTO;
import org.enhance.groovy.api.dto.ProductInfoDTO;

import java.util.Date;

/**
 * 基础controller
 *
 * @author wenpan 2022/10/02 17:28
 */
public class BaseController {
    /**
     * 构建订单入参
     */
    public ExecuteParams buildOrderParams() {
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        orderInfoDTO.setOrderId(1000);
        orderInfoDTO.setOrderAmount("1000");
        orderInfoDTO.setOrderName("测试订单");
        orderInfoDTO.setOrderNumber("BG-123987");
        ExecuteParams executeParams = new ExecuteParams();
        executeParams.put("orderInfo", orderInfoDTO);
        return executeParams;
    }

    /**
     * 构建商品入参
     */
    public ExecuteParams buildProductParams() {
        ProductInfoDTO productInfoDTO = new ProductInfoDTO();
        productInfoDTO.setCreateDate(new Date());
        productInfoDTO.setName("小米手机");
        productInfoDTO.setPrice(10D);
        ExecuteParams executeParams = new ExecuteParams();
        executeParams.put("productInfo", productInfoDTO);
        return executeParams;
    }
}
