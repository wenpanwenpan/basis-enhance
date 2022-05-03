package org.clean.code.mapstruct.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * carVO
 *
 * @author Mr_wenpan@163.com 2022/05/02 15:43
 */
@Data
@Builder
public class CarVO {

    /**
     * 车名称
     */
    private String carName;
    /**
     * 车编号
     */
    private Integer number;
    /**
     * 发布日期(String类型)
     */
    private String publishDate;
    /**
     * 车价格(String类型)
     */
    private String price;
    /**
     * 油耗(String类型)
     */
    private String oilWear;
    /**
     * 车品牌(和DTO里名字不一样)
     */
    private String brandName;
    /**
     * 车颜色
     */
    private String color;
    /**
     * 驾驶员
     */
    private DriverVO driverVO;
    /**
     * 车部件集合
     */
    private List<PartVO> partVOList;
    /**
     * 是否有部件（dto中不存在这个属性）
     */
    private Boolean hasPart;

}
