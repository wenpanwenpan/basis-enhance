package org.enhance.datasource.demo.service;

import org.enhance.datasource.demo.entity.Product;
import org.enhance.datasource.demo.mapper.ProductMapper;
import org.enhance.datasource.demo.page.Page;
import org.enhance.datasource.demo.page.PageHelper;
import org.enhance.datasource.demo.page.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品service
 *
 * @author Mr_wenpan@163.com 2022/04/05 16:10
 */
@Service
public class ProductService {

	@Autowired
	private ProductMapper productMapper;

	public List<Product> findAllProductFromMaster() {
		List<Product> products = productMapper.findAllProductFromMaster();
		System.out.println("products = " + products);
		return products;
	}

	public List<Product> findAllProductFromSlave() {
		List<Product> products = productMapper.findAllProductFromSlave();
		System.out.println("products = " + products);
		return products;
	}

	public List<Product> pageHelperTest(PageInfo pageInfo) {

		Page<Product> page = PageHelper.doPage(pageInfo, () -> productMapper.pageHelperTest(pageInfo, "nul"));

		Page<Product> pageAndCount = PageHelper.doPageAndCount(pageInfo, () -> 1L, () -> productMapper.pageHelperTest(pageInfo, "null"));

		List<Product> products = productMapper.findAllProductFromSlave();
		System.out.println("products = " + products);
		return products;
	}
}
