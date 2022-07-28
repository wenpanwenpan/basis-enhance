package org.demo.threadpool.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.demo.threadpool.app.TestAsyncAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * async注解源码debug用
 *
 * @author wenpan 2022/07/25 19:13
 */
@Slf4j
@RestController
@RequestMapping("/test-async")
public class TestAsyncAnnotationController {

    @Autowired
    private TestAsyncAnnotationService testAsyncAnnotationService;

    @GetMapping
    public String test() {
        // 调用异步执行
        return testAsyncAnnotationService.testAsyncAnnotation();
    }

}
