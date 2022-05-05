package org.clean.code.fastjson.vo;

import lombok.Data;

/**
 * 房屋
 *
 * @author Mr_wenpan@163.com 2022/05/04 16:21
 */
@Data
public class HouseVO {
    /**
     * id
     */
    private Integer id;
    /**
     * 位置
     */
    private String position;
    /**
     * 面积
     */
    private String size;
}
