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

    /**
     * 默认数据源切换db测试
     */
    @GetMapping("/test-01")
    public void test01() {
        try {
            // 手动制造异常
            final int i = 1 / 0;
        } catch (Throwable throwable) {
            throw new CommonException("出错啦，你知道吗？{0}", throwable, "wenpan");
        }
    }

}
