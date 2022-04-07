package org.basis.enhance.event.publisher;

import org.basis.enhance.event.event.BasisEvent;
import org.springframework.lang.NonNull;

import java.util.concurrent.Executor;

/**
 * 事件发布器接口
 *
 * @author Mr_wenpan@163.com 2022/04/01 18:00
 */
public interface EventPublisher {

    /**
     * 事件发布器
     *
     * @param event 事件
     * @author Mr_wenpan@163.com 2022/4/1 6:00 下午
     */
    void publish(@NonNull BasisEvent event);

    /**
     * 异步事件发布器
     *
     * @param event 事件
     * @author Mr_wenpan@163.com 2022/4/1 6:00 下午
     */
    void publishAsync(@NonNull BasisEvent event);

    /**
     * 异步事件发布器
     *
     * @param event        事件
     * @param taskExecutor 线程池
     * @author Mr_wenpan@163.com 2022/4/1 6:00 下午
     */
    void publishAsync(@NonNull BasisEvent event, @NonNull Executor taskExecutor);
}
