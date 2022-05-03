package org.enhance.core.demo.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.enhance.core.demo.domain.entity.Product;
import org.enhance.core.web.page.BasisPageInfo;

import java.util.List;

/**
 * 商品mapper
 *
 * @author Mr_wenpan@163.com 2022/04/05 16:08
 */
public interface ProductMapper {

    /**
     * 分页查询数据
     */
    @Select("select * from product limit #{pageInfo.begin},#{pageInfo.end}")
    List<Product> pageHelperTest(@Param("pageInfo") BasisPageInfo basisPageInfo, @Param("number") String number);

    /**
     * 分页查询数据2
     */
    @Select("select * from product")
    List<Product> pageHelperTest2();
}