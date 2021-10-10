package org.basis.enhance.concurrent.queue;

import org.springframework.lang.NonNull;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 线程优先队列
 *
 * @author Mr_wenpan@163.com 2021/10/09 14:55
 */
public class ThreadPriorityBlockingQueue<E> extends LinkedBlockingQueue<E> {

    public ThreadPriorityBlockingQueue() {

    }

    /**
     * 指定队列容量的构造器
     */
    public ThreadPriorityBlockingQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(@NonNull E t) {
        // 造成队列已满的假象
        // 线程池使用的是LinkedBlockingQueue的offer方法向阻塞队列中添加任务的，一旦该方法返回false，则认为阻塞队列已满
        return false;
    }

}
