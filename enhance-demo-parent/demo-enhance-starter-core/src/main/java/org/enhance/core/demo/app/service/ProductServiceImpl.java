package org.enhance.core.demo.app.service;

import org.enhance.core.demo.domain.entity.Product;
import org.enhance.core.demo.infra.mapper.ProductMapper;
import org.enhance.core.web.page.BasisPageInfo;
import org.enhance.core.web.page.Page;
import org.enhance.core.web.page.PageRequest;
import org.enhance.core.web.page.helper.PageHelper;
import org.enhance.core.web.page.helper.SimplePageHelper;
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
    public Page<Product> getProductByPage(BasisPageInfo basisPageInfo) {
        Page<Product> page = SimplePageHelper.doPage(basisPageInfo, () -> productMapper.pageHelperTest(basisPageInfo, "nul"));
        Page<Product> pageAndCount = SimplePageHelper.doPageAndCount(basisPageInfo, () -> 1L, () -> productMapper.pageHelperTest(basisPageInfo, "null"));
        System.out.println("数据量为 = " + page.size());
//        System.out.println("pageAndCount = " + pageAndCount);
        return page;
    }

    @Override
    public Page<Product> getProductByPageHelper(BasisPageInfo basisPageInfo) {

        PageRequest pageRequest = new PageRequest(basisPageInfo.getPage(), basisPageInfo.getSize());
        Page<Product> products = PageHelper.doPage(pageRequest, () -> productMapper.pageHelperTest2());
        System.out.println("products = " + products);

        return products;
    }

    @Override
    public List<Product> pageHelperTest(BasisPageInfo basisPageInfo) {

        return null;
    }

    @Override
    public Page<Product> getProductByPageRequest(PageRequest pageRequest) {
        pageRequest.setReasonable(false);
        Page<Product> products = PageHelper.doPage(pageRequest, () -> productMapper.pageHelperTest2());
        System.out.println("products = " + products);
        return products;
    }
}