package org.enhance.groovy.app.service.impl;

import org.enhance.groovy.api.dto.ProductInfoDTO;
import org.enhance.groovy.app.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * ProductService实现类
 *
 * @author wenpan 2022/10/01 13:22
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public ProductInfoDTO getProductById(Integer id) {
        // 直接创建一个商品
        ProductInfoDTO productInfoDTO = new ProductInfoDTO();
        productInfoDTO.setId(id);
        productInfoDTO.setName(UUID.randomUUID().toString());
        return productInfoDTO;
    }


    @Override
    public void updateProduct(ProductInfoDTO productInfo) {
        // 修改商品名称
        productInfo.setName("update-" + productInfo.getName());
    }
}
