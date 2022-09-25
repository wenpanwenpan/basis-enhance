package org.enhance.groovy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试groovy
 *
 * @author wenpan 2022/09/25 14:46
 */
@Slf4j
@RestController("TestEnhanceGroovyEngineController.v1")
@RequestMapping("/v1/test-enhance-groovy")
public class TestEnhanceGroovyEngineController {

    /**
     * 默认数据源切换db测试
     */
    @GetMapping("/test-01")
    public String test01() {

        return "success";
    }
}
