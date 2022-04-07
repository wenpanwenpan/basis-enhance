package org.enhance.datasource.demo.controller;

import org.basis.enhance.datasource.annotation.DynamicDataSource;
import org.enhance.datasource.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品controller
 *
 * @author Mr_wenpan@163.com 2022/04/05 16:12
 */
@RestController
public class ProductController {

	@Autowired
	private ProductService productService;

	@DynamicDataSource("master")
	@GetMapping("/find-from-master")
	public String findAllProductFromMaster() {
		productService.findAllProductFromMaster();
		return "find from master success";
	}

	@DynamicDataSource("slave")
	@GetMapping("/find-from-slave")
	public String findAllProductFromSlave() {
		productService.findAllProductFromSlave();
		return "find from slave success";
	}


}
