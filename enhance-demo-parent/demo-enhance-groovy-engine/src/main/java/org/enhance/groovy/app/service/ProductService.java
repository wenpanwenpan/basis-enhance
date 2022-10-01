package org.enhance.groovy.app.service;

import org.enhance.groovy.api.dto.ProductInfoDTO;

/**
 * 商品service
 *
 * @author wenpan 2022/10/01 13:21
 */
public interface ProductService {

    /**
     * 通过ID获取商品
     *
     * @param id id
     * @return org.enhance.groovy.api.dto.ProductInfoDTO
     */
    ProductInfoDTO getProductById(Integer id);

    /**
     * 更新商品
     *
     * @param productInfo 商品
     */
    void updateProduct(ProductInfoDTO productInfo);
}
