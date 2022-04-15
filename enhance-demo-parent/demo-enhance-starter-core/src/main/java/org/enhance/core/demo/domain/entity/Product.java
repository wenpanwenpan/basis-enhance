package org.enhance.core.demo.domain.entity;

import lombok.Data;

import java.util.Date;

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
	private Date createDate;
}
