package org.enhance.executor.demo.infra.handler;

import lombok.extern.slf4j.Slf4j;
import org.basis.enhance.executor.infra.server.handler.TaskHandler;
import org.springframework.stereotype.Component;

/**
 * 测试任务处理器
 *
 * @author Mr_wenpan@163.com 2021/08/20 14:41
 */
@Slf4j
@Component("testTaskHandler")
public class TestTaskHandler implements TaskHandler<String> {

    @Override
    public void handler(String data) {
        log.info("收到分布式任务数据,data = {}", data);
    }
}
