package org.enhance.datasource.demo.service;

import org.enhance.datasource.demo.entity.Product;
import org.enhance.datasource.demo.mapper.ProductMapper;
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
}
