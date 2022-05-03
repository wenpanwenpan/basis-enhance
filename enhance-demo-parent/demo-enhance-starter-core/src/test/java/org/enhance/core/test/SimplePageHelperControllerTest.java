package org.enhance.core.test;

import org.enhance.core.demo.api.controller.TestPageHelperController;
import org.enhance.core.demo.app.service.ProductService;
import org.enhance.core.demo.domain.entity.Product;
import org.enhance.core.web.page.BasisPageInfo;
import org.enhance.core.web.page.Page;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * PageHelperController测试
 *
 * @author Mr_wenpan@163.com 2022/04/17 17:39
 */
@RunWith(MockitoJUnitRunner.class)
public class SimplePageHelperControllerTest {

    private ProductService productService;

    @Before
    public void init() {
        productService = mock(ProductService.class);
    }

    @Test
    public void testPageHelper() {
        TestPageHelperController controller = new TestPageHelperController();
        when(productService.getProductByPage(any())).thenReturn(new Page<>());
        Page<Product> products = controller.testPageHelper(any(BasisPageInfo.class));
        assertThat(products, instanceOf(RuntimeException.class));
    }

}
