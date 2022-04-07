package org.enhance.datasource.demo.entity;

import lombok.Data;

/**
 * 商品表
 *
 * @author Mr_wenpan@163.com 2022/04/05 16:08
 */
@Data
public class Product {
	private Integer id;
	private String name;
	private Double price;
}
