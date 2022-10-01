package org.enhance.groovy.infra.groovy

import org.basis.enhance.groovy.entity.ExecuteParams
import org.enhance.groovy.api.dto.ProductInfoDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ChangeProductInfo extends Script {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // 修改商品信息
    ProductInfoDTO changeProduct(ExecuteParams executeParams) {
        // 获取product对象
        ProductInfoDTO productInfo = (ProductInfoDTO) executeParams.get("productInfo");
        Double newPrice = null;
        Double oldPrice = productInfo.getPrice();

        // 模拟一大推的判断逻辑
        if (oldPrice > 10000D) {
            // 商品金额大于10000则修改为8000
            newPrice = 8000D;
        } else if (oldPrice > 5000D && oldPrice <= 10000D) {
            // 商品金额位于 5000 到 10000之间，则将价格修改为 6000
            newPrice = 6000D;
        } else if (oldPrice > 1000D && oldPrice <= 5000D) {
            // 商品金额位于 1000 到 50000之间，则将价格修改为 3000
            newPrice = 3000D;
        } else {
            // 其余情况金额修改为500
            newPrice = 500D;
        }

        logger.info("即将修改商品金额，原金额为：{}, 修改后的金额为：{}", oldPrice, newPrice);
        // 商品价格修改为 newPrice
        productInfo.setPrice(newPrice);

        // 返回修改后的对象
        return productInfo;
    }

    @Override
    Object run() {
        return null
    }
}
