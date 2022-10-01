package org.enhance.groovy.api.dto;

import lombok.Data;

import java.util.Date;

/**
 * 商品信息
 *
 * @author wenpan 2022/09/30 21:58
 */
@Data
public class ProductInfoDTO {
    private Integer id;
    private String name;
    private Double price;
    private Date createDate;
}
