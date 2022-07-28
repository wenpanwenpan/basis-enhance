package org.demo.threadpool.app.impl;

import lombok.extern.slf4j.Slf4j;
import org.demo.threadpool.app.TestThreadPoolTaskSchedulerService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 测试ThreadPoolTaskScheduler
 *
 * @author wenpan 2022/07/26 11:29
 */
@Slf4j
@Service
public class TestThreadPoolTaskSchedulerServiceImpl implements TestThreadPoolTaskSchedulerService {

    /**
     * Scheduled注解标注的方法应该是无返回值，并且放入入参个数为0的
     */
//    @Scheduled(cron = "0/5 * * * * ?")
    @Override
    public void testThreadPoolTaskScheduler() throws InterruptedException {
        // 休眠2秒
        TimeUnit.SECONDS.sleep(2);
        log.info("@Scheduled 任务执行...");
    }

}
