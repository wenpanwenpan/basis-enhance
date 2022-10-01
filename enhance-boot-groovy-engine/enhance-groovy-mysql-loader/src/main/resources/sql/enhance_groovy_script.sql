SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for enhance_groovy_script
-- ----------------------------
DROP TABLE IF EXISTS `enhance_groovy_script`;
CREATE TABLE `enhance_groovy_script` (
  `id` bigint NOT NULL COMMENT '主键id',
  `namespace` varchar(128) NOT NULL COMMENT '命名空间',
  `platform_code` varchar(128) NOT NULL COMMENT '平台码',
  `product_code` varchar(128) NOT NULL COMMENT '产品码',
  `channel_code` varchar(128) NOT NULL COMMENT '渠道码',
  `business_code` varchar(128) NOT NULL COMMENT '业务码',
  `enable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否启用',
  `script_content` longtext NOT NULL COMMENT '脚本内容',
  `extend_info` longtext COMMENT '扩展信息',
  `talent` varchar(64) NOT NULL DEFAULT 'unknown' COMMENT '租户编码',
  `object_version_number` int NOT NULL DEFAULT '0' COMMENT '版本号',
  `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建日期',
  `latest_modified_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_only_key` (`namespace`,`platform_code`,`product_code`,`channel_code`,`business_code`) USING BTREE COMMENT '唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of enhance_groovy_script
-- ----------------------------
BEGIN;
INSERT INTO `enhance_groovy_script` VALUES (1, 'customer-console', 'console-manager', 'enhance', 'test', 'change-product', 1, 'package org.enhance.groovy.api.dto \n\nimport org.basis.enhance.groovy.entity.ExecuteParams\nimport org.slf4j.Logger\nimport org.slf4j.LoggerFactory\n\nclass ChangeProductInfo extends Script {\n\n    private final Logger logger = LoggerFactory.getLogger(getClass());\n\n    // 修改商品信息\n    ProductInfoDTO changeProduct(ExecuteParams executeParams) {\n        // 获取product对象\n        ProductInfoDTO productInfo = (ProductInfoDTO) executeParams.get(\"productInfo\");\n        Double newOrderAmount = 20000D;\n        logger.info(\"即将修改商品金额，原金额为：{}, 修改后的金额为：{}\", productInfo.getPrice(), newOrderAmount);\n        // 商品价格修改为newOrderAmount\n        productInfo.setPrice(newOrderAmount);\n        // 返回修改后的对象\n        return productInfo;\n    }\n\n    @Override\n    Object run() {\n        return null\n    }\n}', NULL, 'unknown', 0, '2022-10-01 18:52:10', '2022-10-01 18:52:10');
INSERT INTO `enhance_groovy_script` VALUES (2, 'customer-console', 'console-manager', 'enhance', 'test', 'change-order', 1, 'package org.enhance.groovy.api.dto\n\n\nimport org.slf4j.Logger\nimport org.slf4j.LoggerFactory\n\nclass ChangeOrderInfo extends Script {\n\n    private final Logger logger = LoggerFactory.getLogger(getClass());\n\n    @Override\n    Object run() {\n        // 调用方法\n        changeOrderInfo();\n    }\n\n    // 修改订单信息\n    OrderInfoDTO changeOrderInfo() {\n        String newOrderAmount = \"20000\";\n        // 获取参数\n        OrderInfoDTO orderInfoDTO = orderInfo;\n        logger.info(\"即将修改订单金额，原金额为：{}, 修改后的金额为：{}\", orderInfoDTO.getOrderAmount(), newOrderAmount);\n        orderInfoDTO.setOrderAmount(\"2000\");\n        // 返回修改后的结果\n        return orderInfoDTO;\n    }\n}', NULL, 'unknown', 0, '2022-10-01 18:38:25', '2022-10-01 18:38:31');
INSERT INTO `enhance_groovy_script` VALUES (3, 'customer-console', 'console-manager', 'enhance', 'test', 'get-context', 1, 'package org.enhance.groovy.infra.groovy\n\nimport org.enhance.groovy.api.dto.ProductInfoDTO\nimport org.enhance.groovy.app.service.ProductService\nimport org.slf4j.Logger\nimport org.slf4j.LoggerFactory\nimport org.springframework.context.ApplicationContext\n\n/**测试从spring ioc容器中获取bean，并调用bean的方法*/\nclass GetApplicationContext extends Script {\n\n    private final Logger logger = LoggerFactory.getLogger(getClass());\n\n    @Override\n    Object run() {\n        // 调用方法\n        ApplicationContext context = getContext();\n        // 获取容器中的bean\n        ProductService productService = context.getBean(ProductService.class);\n        // 调用bean的方法\n        Random random = new Random();\n        ProductInfoDTO productInfoDTO = productService.getProductById(random.nextInt(1000));\n        logger.info(\"productInfoDTO is : {}\", productInfoDTO);\n\n        // 调用bean 的修改方法\n        productService.updateProduct(productInfoDTO);\n        logger.info(\"updated productInfoDTO is : {}\", productInfoDTO);\n        return productInfoDTO;\n    }\n\n    // 获取spring容器\n    ApplicationContext getContext() {\n        // 获取spring IOC容器\n        ApplicationContext context = applicationContext;\n        return context;\n    }\n}', NULL, 'unknown', 0, '2022-10-01 18:40:17', '2022-10-01 18:40:17');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
