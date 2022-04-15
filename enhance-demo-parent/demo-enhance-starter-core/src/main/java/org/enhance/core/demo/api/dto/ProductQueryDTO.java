package org.enhance.core.demo.api.dto;

import lombok.Data;
import org.enhance.core.demo.infra.validgroup.AValidGroup;
import org.enhance.core.demo.infra.validgroup.BValidGroup;
import org.enhance.core.demo.infra.validgroup.CValidGroup;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 商品查询条件
 *
 * @author Mr_wenpan@163.com 2022/04/15 17:24
 */
@Data
public class ProductQueryDTO {

    @NotBlank
    String productName;
    @Email
    String email;
    @NotNull(message = "商品生产地址不能为空", groups = {AValidGroup.class, CValidGroup.class})
    String productionAddress;
    @Min(value = 0, message = "商品类型最小值0")
    Integer productType;
    @Max(message = "商品价格", value = 300, groups = {BValidGroup.class})
    Integer price;
    /**
     * 商品规格
     */
    String species;
    /**
     * jsr303对象和集合的校验需要加@Valid注解才能生效
     */
    @Valid
    @NotNull(message = "skuQueryDTO 不能为空")
    SkuQueryDTO skuQueryDTO;
    /**
     * jsr303对象和集合的校验需要加@Valid注解才能生效
     */
    @Valid
    @NotNull(message = "list集合不能为空")
    List<SkuQueryDTO> list;

}
