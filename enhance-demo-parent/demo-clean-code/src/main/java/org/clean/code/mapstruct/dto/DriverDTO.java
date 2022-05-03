package org.clean.code.mapstruct.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DriverDTO
 *
 * @author Mr_wenpan@163.com 2022/05/02 15:44
 */
@Data
@Builder
public class DriverDTO {
    /**
     * 司机ID
     */
    private long id;
    /**
     * 司机名称
     */
    private String name;
    /**
     * 司机age
     */
    private int age;
}
