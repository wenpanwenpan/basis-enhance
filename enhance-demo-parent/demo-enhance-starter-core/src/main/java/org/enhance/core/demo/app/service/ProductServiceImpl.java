package org.enhance.core.demo.app.service;

import org.enhance.core.demo.domain.entity.Product;
import org.enhance.core.demo.infra.mapper.ProductMapper;
import org.enhance.core.web.page.Page;
import org.enhance.core.web.page.PageInfo;
import org.enhance.core.web.page.helper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品service
 *
 * @author Mr_wenpan@163.com 2022/04/05 16:10
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Page<Product> getProductByPage(PageInfo pageInfo) {
        Page<Product> page = PageHelper.doPage(pageInfo, () -> productMapper.pageHelperTest(pageInfo, "nul"));
        Page<Product> pageAndCount = PageHelper.doPageAndCount(pageInfo, () -> 1L, () -> productMapper.pageHelperTest(pageInfo, "null"));
        System.out.println("数据量为 = " + page.size());
//        System.out.println("pageAndCount = " + pageAndCount);
        return page;
    }

    @Override
    public List<Product> pageHelperTest(PageInfo pageInfo) {

        return null;
    }
}