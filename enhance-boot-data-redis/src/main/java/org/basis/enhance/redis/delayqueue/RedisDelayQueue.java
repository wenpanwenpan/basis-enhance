package org.basis.enhance.redis.delayqueue;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 抽象redis延时队列封装
 *
 * @param <T> 队列元素的数据类型，必须要可序列化
 * @author Mr_wenpan@163.com 2021/11/16 9:45 上午
 */
public abstract class RedisDelayQueue<T extends Serializable> implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(RedisDelayQueue.class);

    @Autowired
    private RedissonClient redissonClient;

    private RBlockingDeque<T> blockingDeque;

    private RDelayedQueue<T> delayedQueue;

    private ExecutorService executorService;

    private AtomicReference<Status> status;

    /**
     * 消费延时队列中已经到期的任务
     */
    public final void consume() {
        // balking模式，防止重复启动消费任务
        if (!status.compareAndSet(Status.PENDING, Status.RUNNING)) {
            throw new IllegalStateException("RedisDelayQueue Processing Status is [RUNNING]");
        }

        log.info("Redis Delay Queue Processing Start , current delayQueue : {}", queue());

        T take = null;
        while (!Thread.currentThread().isInterrupted() && !executorService.isShutdown()) {

            try {
                // 在添加时，会将 [任务 + 过期时间] 添加到delayedQueue中（zset结构），
                // 由Redisson客户端将到期的任务从delayedQueue转移到blockingDeque，所以blockingDeque中全是到期的任务
                take = blockingDeque.take();

                if (executorService.isShutdown() || Thread.currentThread().isInterrupted()) {
                    // 重新放入队列，等待其他节点消费
                    delayedQueue.offer(take, 0, timeUnit());
                    break;
                } else if (Objects.nonNull(take)) {
                    // 每个队列都对应着一个线程池来进行消费，加快消费速度避免消息积压
                    T submit = take;
                    executorService.submit(() -> doConsume(submit));
                }
            } catch (InterruptedException e) {
                log.error("take delayQueue Interrupted, exception :", e);
            } catch (RejectedExecutionException e) {
                delayedQueue.offer(take, 0, timeUnit());
                log.error("delayQueue submit task rejected, exception : ", e);
            } catch (Throwable t) {
                // 其他异常，比如：阻塞take时系统突然关机
                log.error("RedisDelayQueue has error , exception :", t);
            }
        }

        status.set(Status.STOP);
    }

    /**
     * 数据入队
     *
     * @param data 数据
     * @return true/false
     */
    public final boolean offer(T data) {
        return offer(data, delayedTime(), timeUnit());
    }

    public final boolean offer(T data, int delayed, TimeUnit timeUnit) {
        if (log.isDebugEnabled()) {
            log.debug("redisDelayQueue add data :{}, and delay :{} ", data, delayed + timeUnit.toString());
        }
        try {
            delayedQueue.offer(data, delayed, timeUnit);
        } catch (Exception e) {
            log.error("add redis delay queue error , msg :", e);
            return false;
        }
        return true;
    }

    /**
     * 数据移出延时队列
     */
    public final boolean remove(T data) {
        try {
            return delayedQueue.remove(data);
        } catch (Exception e) {
            log.error("remove redis delay queue error ,msg :", e);
            return false;
        }
    }

    protected void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    protected int delayedTime() {
        return 30;
    }

    protected TimeUnit timeUnit() {
        return TimeUnit.MINUTES;
    }

    @Override
    public final void afterPropertiesSet() throws Exception {
        Assert.notNull(redissonClient, "require redissonClient not null");
        String queue = queue();
        Assert.isTrue(StringUtils.hasText(queue), "delayedQueue require queue not null");
        blockingDeque = redissonClient.getBlockingDeque(queue);
        delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        executorService = createExecutorService();
        // 注册到队列管理器
        RedisDelayQueueManager.register(queue, executorService);
        status = new AtomicReference<>(Status.PENDING);
    }

    @PreDestroy
    protected void destroy() {
        status = null;
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
        delayedQueue.destroy();
    }

    /**
     * 子类可覆写，使用自定义线程池
     *
     * @return java.util.concurrent.ExecutorService
     */
    protected ExecutorService createExecutorService() {
        int threads = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(1, threads, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder()
                        .setNameFormat(getClass().getSimpleName() + "-%s")
                        .setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
                            log.error(" catching the uncaught exception, ThreadName: [{}]", thread.toString(), throwable);
                        })
                        .build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 数据处理
     *
     * @param data 数据
     */
    protected abstract void doConsume(T data);

    /**
     * 延时队列名
     *
     * @return 队列名
     */
    public abstract String queue();

    /**
     * 执行状态
     */
    enum Status {
        /**
         * 待执行
         */
        PENDING,
        /**
         * 运行中
         */
        RUNNING,
        /**
         * 停止
         */
        STOP
    }
}


