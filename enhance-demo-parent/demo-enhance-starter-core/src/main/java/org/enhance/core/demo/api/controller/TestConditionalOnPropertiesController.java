package org.enhance.core.demo.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.enhance.core.demo.domain.entity.Product;
import org.enhance.core.demo.domain.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试ConditionalOnProperties注解controller
 *
 * @author wenpanfeng 2022/09/30 12:48
 */
@Slf4j
@RestController
@RequestMapping("/test-annotation")
public class TestConditionalOnPropertiesController {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 验证自定义注解 {@link  org.enhance.core.annotation.ConditionalOnProperties}是否能正确的控制bean注入容器
     */
    @GetMapping("/test")
    public String testBaseObj() {
        Product product = null;
        UserInfo userInfo = null;
        try {
            product = applicationContext.getBean(Product.class);
            userInfo = applicationContext.getBean(UserInfo.class);
        } catch (Exception ex) {
            log.error("product is : {}, userInfo is : {}", product, userInfo);
            log.error("获取bean异常，异常信息：", ex);
        }

        log.info("成功获取bean, product is : {}, userInfo is : {}", product, userInfo);
        return "success";
    }
}