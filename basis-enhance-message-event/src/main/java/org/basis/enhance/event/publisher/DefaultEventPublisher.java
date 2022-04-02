package org.basis.enhance.event.publisher;

import org.basis.enhance.event.event.BasisEvent;
import org.basis.enhance.event.executor.MessageEventTaskExecutor;
import org.basis.enhance.event.registrar.ApplicationEventMulticaster;
import org.springframework.lang.NonNull;

import java.util.concurrent.Executor;

/**
 * 默认事件发布器
 *
 * @author Mr_wenpan@163.com 2022/04/01 18:01
 */
public class DefaultEventPublisher implements EventPublisher {

    @NonNull
    private final MessageEventTaskExecutor messageEventTaskExecutor;

    @NonNull
    private final ApplicationEventMulticaster eventMulticaster;

    public DefaultEventPublisher(@NonNull ApplicationEventMulticaster eventMulticaster,
                                 @NonNull MessageEventTaskExecutor messageEventTaskExecutor) {
        this.eventMulticaster = eventMulticaster;
        this.messageEventTaskExecutor = messageEventTaskExecutor;
    }

    @Override
    public void publish(@NonNull BasisEvent event) {
        eventMulticaster.multicastEventEfficient(event);
    }

    @Override
    public void publishAsync(@NonNull BasisEvent event) {
        getMessageEventTaskExecutor().execute(() -> eventMulticaster.multicastEventEfficient(event));
    }

    @Override
    public void publishAsync(@NonNull BasisEvent event, @NonNull Executor taskExecutor) {
        taskExecutor.execute(() -> eventMulticaster.multicastEventEfficient(event));
    }

    @NonNull
    public ApplicationEventMulticaster getEventMulticaster() {
        return eventMulticaster;
    }

    @NonNull
    public Executor getMessageEventTaskExecutor() {
        return messageEventTaskExecutor;
    }

}
