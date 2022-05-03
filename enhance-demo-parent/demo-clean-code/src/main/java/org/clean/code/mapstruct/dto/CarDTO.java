package org.clean.code.mapstruct.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * carDTO
 *
 * @author Mr_wenpan@163.com 2022/05/02 15:43
 */
@Data
@Builder
public class CarDTO {
    /**
     * 车名称
     */
    private String carName;
    /**
     * 车编号
     */
    private Integer number;
    /**
     * 发布日期(date类型)
     */
    private Date publishDate;
    /**
     * 车价格
     */
    private BigDecimal price;
    /**
     * 油耗
     */
    private double oilWear;
    /**
     * 车品牌
     */
    private String brand;
    /**
     * 车颜色
     */
    private String color;
    /**
     * 驾驶员
     */
    private DriverDTO driverDTO;
    /**
     * 车部件集合
     */
    private List<PartDTO> partDTOList;

}
