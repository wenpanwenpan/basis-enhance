package org.clean.code.mapstruct.vo;

import lombok.Builder;
import lombok.Data;

/**
 * driverVO
 *
 * @author Mr_wenpan@163.com 2022/05/02 15:44
 */
@Data
@Builder
public class DriverVO {
    /**
     * 司机ID
     */
    private Long driverId;
    /**
     * 司机名称
     */
    private String driverName;
    /**
     * 司机age
     */
    private Integer age;
}
