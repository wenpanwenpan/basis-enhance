package org.enhance.core.demo.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * sku查询dto
 *
 * @author Mr_wenpan@163.com 2022/04/15 17:34
 */
@Data
public class SkuQueryDTO {

    @NotNull(message = "skuCode 不能为空")
    String skuCode;
    @NotNull(message = "description 不能为空")
    String description;
    @NotNull(message = "type 不能为空")
    String type;

}
