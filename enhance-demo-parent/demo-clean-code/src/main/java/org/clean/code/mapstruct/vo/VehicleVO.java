package org.clean.code.mapstruct.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * VehicleVO
 *
 * @author Mr_wenpan@163.com 2022/05/02 16:51
 */
@Data
public class VehicleVO {

    /**
     * 车名称
     */
    private String name;
    /**
     * 车编号
     */
    private Integer number;
    /**
     * 车价格
     */
    private BigDecimal price;
    /**
     * 品牌名称
     */
    private String brandName;

}
