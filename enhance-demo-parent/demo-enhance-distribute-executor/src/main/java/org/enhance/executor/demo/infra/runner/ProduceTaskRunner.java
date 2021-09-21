package org.enhance.executor.demo.infra.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.basis.enhance.executor.config.property.ExecutorProperties;
import org.basis.enhance.executor.domain.entity.Task;
import org.basis.enhance.executor.infra.client.MainTask;
import org.basis.enhance.executor.infra.client.TaskClient;
import org.enhance.executor.demo.infra.handler.TestTaskHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产任务的runner
 *
 * @author Mr_wenpan@163.com 2021/08/20 14:35
 */
@Slf4j
@Component
public class ProduceTaskRunner implements CommandLineRunner, DisposableBean {

    @Autowired
    private TaskClient taskClient;

    @Autowired
    private ExecutorProperties properties;

    private final AtomicInteger taskCount = new AtomicInteger(0);

    ScheduledExecutorService register = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
            .namingPattern("produce-task-pool-")
            .daemon(true)
            .build());

    @Override
    public void run(String... args) throws Exception {
        // 定时任务线程池，每隔5s生产一个任务
        register.scheduleAtFixedRate(() -> {
            log.info("生产第 {} 个分布式任务......", taskCount.incrementAndGet());
            try {
                MainTask mainTask = iRun();
//            mainTask.submit().waitTaskFinished(2, TimeUnit.SECONDS);
                log.info("生产的数据信息：{}", mainTask);
                mainTask.submit();
            } catch (Exception ex) {
                log.error("出现异常啦：异常信息：", ex);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public MainTask iRun() {
        MainTask mainTask = taskClient.createMainTask();
        Task.Builder builder = new Task.Builder();
        builder.isIdempotent(Boolean.FALSE);
        builder.taskHandlerBeanId(getHandler());
        builder.data("wenpan-test-data---" + UUID.randomUUID());
        builder.group(properties.getGroup());
        builder.title("test-title");
        mainTask.addSubTask(builder.build());
        return mainTask;
    }

    public String getHandler() {
        String handler = TestTaskHandler.class.getAnnotation(Component.class).value();
        Assert.notNull(handler, "handler 不能为空.....");
        return handler;
    }

    @Override
    public void destroy() throws Exception {
        register.shutdown();
    }
}
