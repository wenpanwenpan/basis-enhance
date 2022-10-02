package org.enhance.groovy.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.groovy.entity.EngineExecutorResult;
import org.basis.enhance.groovy.entity.ExecuteParams;
import org.basis.enhance.groovy.entity.ScriptEntry;
import org.basis.enhance.groovy.entity.ScriptQuery;
import org.basis.enhance.groovy.executor.EngineExecutor;
import org.basis.enhance.groovy.loader.ScriptLoader;
import org.enhance.groovy.api.dto.OrderInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 测试性能controller
 *     <ol>
 *         <li>使用Redis-loader作为脚本数据来源</li>
 *         <li>测试每次都编译脚本为Class然后再执行脚本的效率和使用缓存缓存脚本的Class脚本效率对比</li>
 *         <li>测试使用了缓存机制的方法区Class数量和直接编译脚本的方法区Class数量对比</li>
 *     </ol>
 * </p>
 *
 * @author wenpan 2022/10/02 17:19
 */
@Slf4j
@RestController("TestPerformanceController.v1")
@RequestMapping("/v1/performance")
public class TestPerformanceController extends BaseController {

    @Autowired
    private EngineExecutor engineExecutor;

    @Autowired
    private ScriptLoader scriptLoader;

    private long testExecuteCount = 0;
    private long compileExecuteCount = 0;
    private long useCacheExecuteCount = 0;

    /**
     * 简单压测一下本机指标
     * 请求URL：http://localhost:1234/v1/performance/test
     */
    @GetMapping("/test")
    public String simpleTest() {
        // 尽量模拟相同情况
        ExecuteParams executeParams = buildOrderParams();
        OrderInfoDTO orderInfo = changeOrderInfo2(executeParams.getValue("orderInfo"));
        log.info("[{}] simpleTest=========>>>>>>>>>>>执行结果：{}", testExecuteCount++, orderInfo);
        return "success";
    }

    /**
     * scriptName只要能唯一定位到脚本即可
     * 测试每次都重新编译脚本为Class，观察方法区的Class数量，以及吞吐量对比
     * 请求URL：http://localhost:1234/v1/performance/compile-direct?scriptName=change-order
     */
    @GetMapping("/compile-direct")
    public String testCompileDirect(String scriptName) throws Exception {
        // 构建参数
        ExecuteParams executeParams = buildOrderParams();
        // 每次都重新编译脚本为Class，观察方法区的Class数量
        ScriptEntry scriptEntry = scriptLoader.load(new ScriptQuery(scriptName));
        // 执行脚本
        EngineExecutorResult executorResult = engineExecutor.execute(scriptEntry, executeParams);
        log.info("[{}] 每次都编译脚本=========>>>>>>>>>>>执行结果：{}", compileExecuteCount++, executorResult);

        return "success";
    }

    /**
     * scriptName只要能唯一定位到脚本即可
     * 请求URL：http://localhost:1234/v1/performance/use-cache?scriptName=change-order
     */
    @GetMapping("/use-cache")
    public String userCache(String scriptName) {
        // 构建参数
        ExecuteParams executeParams = buildOrderParams();
        // 执行脚本
        EngineExecutorResult executorResult = engineExecutor.execute(new ScriptQuery(scriptName), executeParams);
        log.info("[{}] 使用缓存脚本=========>>>>>>>>>>>执行结果：{}", useCacheExecuteCount++, executorResult);

        return "success";
    }

    /**
     * 修改订单信息
     */
    private OrderInfoDTO changeOrderInfo2(OrderInfoDTO orderInfoDTO) {
        String newOrderAmount;

        // 模拟一大堆的判断逻辑
        if (orderInfoDTO.getOrderId() < 10) {
            newOrderAmount = "2000";
        } else if (orderInfoDTO.getOrderId() < 20 && orderInfoDTO.getOrderId() >= 10) {
            newOrderAmount = "5000";
        } else if (orderInfoDTO.getOrderId() < 30 && orderInfoDTO.getOrderId() >= 20) {
            newOrderAmount = "8000";
        } else {
            newOrderAmount = "10000";
        }

        log.info("即将修改订单金额，原金额为：{}, 修改后的金额为：{}", orderInfoDTO.getOrderAmount(), newOrderAmount);
        orderInfoDTO.setOrderAmount(newOrderAmount);
        // 返回修改后的结果
        return orderInfoDTO;
    }

}
