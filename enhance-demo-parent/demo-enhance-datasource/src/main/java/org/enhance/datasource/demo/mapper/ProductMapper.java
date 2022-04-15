package org.enhance.datasource.demo.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.enhance.datasource.demo.entity.Product;
import org.enhance.datasource.demo.page.PageInfo;

import java.util.List;

/**
 * 商品mapper
 *
 * @author Mr_wenpan@163.com 2022/04/05 16:08
 */
public interface ProductMapper {

    /**
     * 从主库查询所有商品
     */
    @Select("select * from product")
    List<Product> findAllProductFromMaster();

    /**
     * 从从库查询所有商品
     */
    @Select("select * from product")
    List<Product> findAllProductFromSlave();

    /**
     * 从从库查询所有商品
     */
    @Select("select * from product")
    List<Product> pageHelperTest(@Param("pageInfo") PageInfo pageInfo, @Param("number") String number);
}