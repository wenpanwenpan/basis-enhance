package org.enhance.core.test.restful;

import org.enhance.core.base.vo.ApiParam;
import org.enhance.core.demo.EnhanceStarterCoreDemoApplication;
import org.enhance.core.helper.DefaultRequestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * DefaultRequestHelper测试
 *
 * @author Mr_wenpan@163.com 2022/05/06 09:53
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EnhanceStarterCoreDemoApplication.class)
public class DefaultRequestHelperTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void test() {
        DefaultRequestHelper defaultRequestHelper = new DefaultRequestHelper();
        ApiParam apiParam = new ApiParam();
        final String url = "http://localhost:8080/test-page/test-02";
        final String url2 = "http://192.168.4.223:8080/models";
        apiParam.put("imgA", "https://image-demo.oss-cn-hangzhou.aliyuncs.com/example.jpg");
        apiParam.put("imgB", "https://image-demo.oss-cn-hangzhou.aliyuncs.com/example.jpg");
        apiParam.put("threshold", "1.6");
        ResponseEntity<Object> responseEntity = defaultRequestHelper.request(url2, HttpMethod.POST, apiParam);
        System.out.println("responseEntity = " + responseEntity);
        Object body = responseEntity.getBody();
        System.out.println("body = " + body);


        System.out.println("xxx".equals(null));
    }

}
