package org.clean.code.jackson.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 车
 *
 * @author Mr_wenpan@163.com 2022/05/04 16:23
 */
@Data
public class CarDTO {
    /**
     * id
     */
    private Long id;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 品牌
     */
    private String brand;
}
