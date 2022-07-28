package org.demo.threadpool.app.impl;

import lombok.extern.slf4j.Slf4j;
import org.demo.threadpool.app.TestAsyncAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * impl
 *
 * @author wenpan 2022/07/25 19:15
 */
@Slf4j
@Service
public class TestAsyncAnnotationServiceImpl implements TestAsyncAnnotationService {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 测试async注解源码
     *
     * @return java.lang.String
     * @author wenpan 2022/7/25 7:16 下午
     */
    @Override
    @Async()
    public String testAsyncAnnotation() {
        log.info("开始执行异步方法testAsyncAnnotation，当前线程name：{}", Thread.currentThread().getName());
        return "success";
    }
}
