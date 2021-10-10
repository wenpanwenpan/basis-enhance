package org.basis.enhance.concurrent.example;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.concurrent.executor.AsyncTask;
import org.basis.enhance.concurrent.executor.CommonExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * CommonExecutor使用示例
 *
 * @author Mr_wenpan@163.com 2021/10/09 22:01
 */
@Slf4j
public class CommonExecutorExample {

    public static void main(String[] args) {
        testCommonExecutor();

//        testThrowable();

    }

    /**
     * 测试公用线程执行器CommonExecutor
     */
    public static void testCommonExecutor() {
        // 构建要执行的异步任务
        List<AsyncTask<String>> asyncTasks = new ArrayList<>();
        asyncTasks.add(() -> "success1");
        asyncTasks.add(() -> "success2");
        asyncTasks.add(() -> "success3");
        // 类似匿名内部类，覆写接口方法
        AsyncTask<String> asyncTask = new AsyncTask<String>() {
            @Override
            public String taskName() {
                return "my-task-name";
            }

            @Override
            public String doExecute() {
                return "success4";
            }
        };
        asyncTasks.add(asyncTask);

        // 使用公用的线程池来执行任务并阻塞等待获取结果
        List<String> result = CommonExecutor.batchExecuteAsync(asyncTasks, "CommonExecutor-test");
        System.out.println("使用公用线程池来执行异步任务结果：" + result);

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("thread-pool-name" + "-%d")
                .setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
                    log.error("线程池已满，任务被拒绝");
                })
                .build();

        // 自己定义执行任务的线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024), threadFactory, (r, executor1) -> log.error("线程池已满，任务被拒绝"));
        // 使用自定义的线程池来执行任务
        List<String> asyncResut = CommonExecutor.batchExecuteAsync(asyncTasks, executor, "CommonExecutor-self-executor");
        System.out.println("使用自定义线程池来执行异步任务结果：" + asyncResut);
    }

    /**
     * 测试异常抛出，直接抛出Throwable，而不是使用throw new xxxException()的方式
     */
    public static void testThrowable() {
        try {
            final int i = 1 / 0;
        } catch (Throwable throwable) {
            throw throwable;
        }
        // 从继承关系可以看到Throwable是一个顶层异常
        System.out.println("异常Throwable抛出后还能执行吗");
    }
}
