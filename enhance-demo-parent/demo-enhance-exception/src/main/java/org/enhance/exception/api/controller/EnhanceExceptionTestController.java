package org.enhance.exception.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.exception.base.CommonException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局异常捕获测试controller
 *
 * @author Mr_wenpan@163.com 2021/10/10 17:50
 */
@Slf4j
@RestController("EnhanceExceptionTestController.v1")
@RequestMapping("/v1/test-enhance-exception")
public class EnhanceExceptionTestController {

    @GetMapping("/test-01")
    public void test01() {
        // 手动制造异常
        try {
            // 手动制造异常
            final int i = 1 / 0;
        } catch (Throwable throwable) {
            // 抛出自定义异常，由异常拦截器拦截并格式化异常信息输出，统一异常格式返回
            throw new CommonException("出错啦，你知道吗？{0}", "wenpan");
        }
    }

}
