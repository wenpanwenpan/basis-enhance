package org.enhance.groovy.infra.groovy

import org.enhance.groovy.api.dto.ProductInfoDTO
import org.enhance.groovy.app.service.ProductService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

/**测试从spring ioc容器中获取bean，并调用bean的方法*/
class GetApplicationContext extends Script {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    Object run() {
        // 调用方法
        ApplicationContext context = getContext();
        // 获取容器中的bean
        ProductService productService = context.getBean(ProductService.class);
        // 调用bean的方法
        Random random = new Random();
        ProductInfoDTO productInfoDTO = productService.getProductById(random.nextInt(1000));
        logger.info("productInfoDTO is : {}", productInfoDTO);

        // 调用bean 的修改方法
        productService.updateProduct(productInfoDTO);
        logger.info("updated productInfoDTO is : {}", productInfoDTO);
        return productInfoDTO;
    }

    // 获取spring容器
    ApplicationContext getContext() {
        // 获取spring IOC容器
        ApplicationContext context = applicationContext;
        return context;
    }
}
