package org.enhance.groovy.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.groovy.entity.EngineExecutorResult;
import org.basis.enhance.groovy.entity.ExecuteParams;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.executor.EngineExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试从redis中加载脚本
 *
 * @author wenpan 2022/09/25 14:46
 */
@Slf4j
@RestController("TestLoadFromRedisController.v1")
@RequestMapping("/v1/load-from-redis")
public class TestLoadFromRedisController extends BaseController {

    @Autowired
    private EngineExecutor engineExecutor;

    /**
     * 测试通过执行脚本run方法来执行脚本
     * scriptName只要能唯一定位到脚本即可
     * 测试{@link EngineExecutor#execute(ScriptQuery, ExecuteParams)}
     * 请求URL：http://localhost:1234/v1/load-from-redis/change-order?scriptName=change-order
     */
    @GetMapping("/change-order")
    public String changeOrderInfo(String scriptName) {
        // 构建参数
        ExecuteParams executeParams = buildOrderParams();
        // 执行脚本
        EngineExecutorResult executorResult = engineExecutor.execute(new ScriptQuery(scriptName), executeParams);
        String statusCode = executorResult.getExecutionStatus().getCode();
        if ("200".equals(statusCode)) {
            log.info("脚本执行成功......");
        } else {
            log.info("脚本执行失败......");
        }
        log.info("使用groovy脚本来动态修改订单信息=========>>>>>>>>>>>执行结果：{}", executorResult);

        return "success";
    }

    /**
     * <p>
     * 测试通过指定脚本方法名来执行脚本
     * scriptName只要能唯一定位到脚本即可
     * 测试{@link EngineExecutor#execute(String, ScriptEntry, ExecuteParams)}
     * 请求URL：http://localhost:1234/v1/load-from-redis/change-product?scriptName=change-product
     * </p>
     */
    @GetMapping("/change-product")
    public String changeProductInfo(String scriptName) {
        // 构建参数
        ExecuteParams executeParams = buildProductParams();
        // 执行脚本中指定的方法 changeProduct
        EngineExecutorResult executorResult = engineExecutor.execute(
                "changeProduct", new ScriptQuery(scriptName), executeParams);
        log.info("使用groovy脚本来动态修改闪屏信息=========>>>>>>>>>>>执行结果：{}", executorResult);

        return "success";
    }

    /**
     * scriptName只要能唯一定位到脚本即可
     * 测试在groovy脚本里获取spring IOC容器上下文，并通过容器来获取bean，并调用bean的方法
     * 请求URL：http://localhost:1234/v1/load-from-redis/get-context?scriptName=get-context
     */
    @GetMapping("/get-context")
    public String getContext(String scriptName) {

        EngineExecutorResult executorResult = engineExecutor.execute(new ScriptQuery(scriptName), null);
        log.info("getContext=========>>>>>>>>>>>执行结果：{}", executorResult);

        return "success";
    }
}
