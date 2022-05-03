package org.demo.threadpool.runner;

import lombok.Getter;

/**
 * 测试枚举
 *
 * @author Mr_wenpan@163.com 2022/04/27 10:50
 */
@Getter
public enum TestEnum {
    one("1", "one_1"),
    two("2", "two_2");

    private String code;
    private String name;

    TestEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
