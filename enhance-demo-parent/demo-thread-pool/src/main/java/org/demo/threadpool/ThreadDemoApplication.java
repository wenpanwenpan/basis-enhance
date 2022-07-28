package org.demo.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 线程池测试启动类
 *
 * @author Mr_wenpan@163.com 2021/08/19 16:17
 */
@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class ThreadDemoApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(ThreadDemoApplication.class, args);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>容器启动成功");
        // 主线程等待三秒，确保线程池任务不能执行完
//        TimeUnit.SECONDS.sleep(5);
//        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>开始关闭容器啦");
//        context.close();
//        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>容器已经关闭啦");
    }

}