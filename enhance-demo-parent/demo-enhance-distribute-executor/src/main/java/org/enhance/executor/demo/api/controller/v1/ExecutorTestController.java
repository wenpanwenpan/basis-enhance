package org.enhance.executor.demo.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.executor.domain.entity.Task;
import org.basis.enhance.executor.infra.client.MainTask;
import org.basis.enhance.executor.infra.client.MainTaskResult;
import org.basis.enhance.executor.infra.client.TaskClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 执行器测试controller
 *
 * @author Mr_wenpan@163.com 2021/09/21 22:39
 */

@Slf4j
@RestController("ExecutorTestController.v1")
@RequestMapping("/v1/test-executor")
public class ExecutorTestController {

    @Autowired
    private TaskClient taskClient;

    @GetMapping
    @ApiOperation(value = "分布式任务测试")
    public String get(String data) {

        // 通过任务客户端构建要执行的分布式任务
        MainTask mainTask = taskClient.createMainTask();
        Task.Builder builder = new Task.Builder();
        builder.isIdempotent(Boolean.FALSE);
        // 指定beanID
        builder.taskHandlerBeanId("testTaskHandler");
        builder.data(data);
        builder.title("task-name");
        mainTask.addSubTask(builder.build());

        // 提交任务到任务池
        MainTaskResult taskResult = mainTask.submit();
        // 阻塞等待分布式任务执行
        taskResult.waitTaskFinished(5, TimeUnit.SECONDS);

        return "success";
    }
}
