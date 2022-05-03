package org.demo.threadpool.runner;

import lombok.NonNull;

/**
 * @author Mr_wenpan@163.com 2022/04/25 10:06
 */
public class TestNonnullimpl implements TestNonnull {

    @Override
    public @NonNull String hello(@NonNull String name) {
        return null;
    }
}
