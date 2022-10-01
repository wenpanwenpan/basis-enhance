package org.enhance.groovy.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.groovy.entity.EngineExecutorResult;
import org.basis.enhance.groovy.entity.ExecuteParams;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.executor.EngineExecutor;
import org.enhance.groovy.api.dto.OrderInfoDTO;
import org.enhance.groovy.api.dto.ProductInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 测试从MySQL中加载脚本
 *
 * @author wenpan 2022/09/25 14:46
 */
@Slf4j
@RestController("TestLoadFromMysqlController.v1")
@RequestMapping("/v1/load-from-mysql")
public class TestLoadFromMysqlController {

    @Autowired
    private EngineExecutor engineExecutor;

    /**
     * 测试从数据库加载脚本
     * scriptName值（和数据库保持一致）：customer-console_console-manager_enhance_test_change-order，scriptName只要能唯一定位到脚本即可
     * 请求URL：http://localhost:1234/v1/load-from-redis/change-order?scriptName=customer-console_console-manager_enhance_test_change-order
     */
    @GetMapping("/change-order")
    public String changeOrderInfo(String scriptName) {
        // 构建参数
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        orderInfoDTO.setOrderAmount("1000");
        orderInfoDTO.setOrderName("测试订单");
        orderInfoDTO.setOrderNumber("BG-123987");
        ExecuteParams executeParams = new ExecuteParams();
        executeParams.put("orderInfo", orderInfoDTO);

        // 执行脚本
        EngineExecutorResult executorResult = engineExecutor.execute(new ScriptQuery(scriptName), executeParams);
        log.info("changeOrderInfo=========>>>>>>>>>>>执行结果：{}", executorResult);

        return "success";
    }

    /**
     * 测试从数据库加载脚本
     * scriptName值（和数据库保持一致）：customer-console_console-manager_enhance_test_change-product，scriptName只要能唯一定位到脚本即可
     * 请求URL：http://localhost:1234/v1/load-from-redis/change-product?scriptName=customer-console_console-manager_enhance_test_change-product
     */
    @GetMapping("/change-product")
    public String changeProductInfo(String scriptName) {
        // 构建参数
        ProductInfoDTO productInfoDTO = new ProductInfoDTO();
        productInfoDTO.setCreateDate(new Date());
        productInfoDTO.setName("小米手机");
        productInfoDTO.setPrice(10D);
        ExecuteParams executeParams = new ExecuteParams();
        executeParams.put("productInfo", productInfoDTO);

        // 执行脚本中指定的方法 changeProduct
        EngineExecutorResult executorResult = engineExecutor.execute(
                "changeProduct", new ScriptQuery(scriptName), executeParams);
        log.info("changeProductInfo=========>>>>>>>>>>>执行结果：{}", executorResult);

        return "success";
    }

    /**
     * 测试从数据库加载脚本
     * scriptName值（和数据库保持一致）：customer-console_console-manager_enhance_test_get-context，scriptName只要能唯一定位到脚本即可
     * 请求URL：http://localhost:1234/v1/load-from-redis/get-context?scriptName=customer-console_console-manager_enhance_test_get-context
     */
    @GetMapping("/get-context")
    public String getContext(String scriptName) {

        EngineExecutorResult executorResult = engineExecutor.execute(new ScriptQuery(scriptName), null);
        log.info("getContext=========>>>>>>>>>>>执行结果：{}", executorResult);

        return "success";
    }
}
