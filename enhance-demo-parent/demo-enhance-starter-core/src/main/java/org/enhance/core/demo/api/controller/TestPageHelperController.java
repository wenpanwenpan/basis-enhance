package org.enhance.core.demo.api.controller;

import org.enhance.core.demo.app.service.ProductService;
import org.enhance.core.demo.domain.entity.Product;
import org.enhance.core.web.page.BasisPageInfo;
import org.enhance.core.web.page.Page;
import org.enhance.core.web.page.PageRequest;
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
    public Page<Product> testPageHelper(BasisPageInfo basisPageInfo) {
        Page<Product> productByPage = productService.getProductByPage(basisPageInfo);
        // 由于page对象实现了list接口，所以可以使用迭代器进行遍历
        productByPage.forEach(e -> {
            System.out.println("====>>>>" + e);
        });
        return productByPage;
    }

    @GetMapping("/test-01")
    public Page<Product> getProductByPageHelper(BasisPageInfo basisPageInfo) {
        Page<Product> productByPage = productService.getProductByPageHelper(basisPageInfo);
        // 由于page对象实现了list接口，所以可以使用迭代器进行遍历
        productByPage.forEach(e -> {
            System.out.println("====>>>>" + e);
        });
        return productByPage;
    }

    @GetMapping("/test-02")
    public Page<Product> getProductByPageRequest(PageRequest pageRequest) {
        Page<Product> productByPage = productService.getProductByPageRequest(pageRequest);
        productByPage.forEach(e -> {
            System.out.println("====>>>>" + e);
        });
        return productByPage;
    }

}
