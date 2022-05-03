package org.enhance.core.demo.app.service;

import org.enhance.core.demo.domain.entity.Product;
import org.enhance.core.web.page.BasisPageInfo;
import org.enhance.core.web.page.Page;
import org.enhance.core.web.page.PageRequest;

import java.util.List;

/**
 * spu商品服务
 *
 * @author Mr_wenpan@163.com 2022/04/15 23:10
 */
public interface ProductService {

    /**
     * 分页获取spu
     *
     * @param basisPageInfo 分页信息
     * @return org.enhance.core.web.page.Page<org.enhance.core.demo.domain.entity.Product>
     */
    Page<Product> getProductByPage(BasisPageInfo basisPageInfo);

    /**
     * 测试分页插件
     */
    Page<Product> getProductByPageHelper(BasisPageInfo basisPageInfo);

    /**
     * 分页helper测试
     *
     * @param basisPageInfo 分页信息
     * @return java.util.List<org.enhance.core.demo.domain.entity.Product>
     */
    List<Product> pageHelperTest(BasisPageInfo basisPageInfo);

    /**
     * 测试通过PageRequest来查询
     */
    Page<Product> getProductByPageRequest(PageRequest pageRequest);
}
