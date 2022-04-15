package org.enhance.core.demo.api.controller;

import org.enhance.core.demo.app.service.ProductService;
import org.enhance.core.demo.domain.entity.Product;
import org.enhance.core.web.page.Page;
import org.enhance.core.web.page.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试分页助手controller
 *
 * @author Mr_wenpan@163.com 2022/04/15 23:09
 */
@RestController
@RequestMapping("/test-page")
public class TestPageHelperController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String testPageHelper(PageInfo pageInfo) {
        Page<Product> productByPage = productService.getProductByPage(pageInfo);
        return productByPage.toString();
    }

}
