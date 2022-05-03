package org.demo.threadpool.runner;

import lombok.NonNull;

/**
 * 测试
 *
 * @author Mr_wenpan@163.com 2022/04/25 10:05
 */
public interface TestNonnull {

    @NonNull String hello(@NonNull String name);
}
