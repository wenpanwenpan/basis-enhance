package org.enhance.core.demo.infra.mapper;

import org.apache.ibatis.annotations.Select;
import org.enhance.core.demo.domain.entity.Product;

import java.util.List;

/**
 * 商品mapper
 *
 * @author Mr_wenpan@163.com 2022/04/05 16:08
 */
public interface ProductMapper {

    /**
     * 分页查询数据2
     *
     * @return List
     */
    @Select("select * from product")
    List<Product> pageHelperTest2();
}