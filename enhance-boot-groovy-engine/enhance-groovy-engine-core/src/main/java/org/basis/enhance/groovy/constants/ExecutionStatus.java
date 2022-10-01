package org.basis.enhance.groovy.constants;

import lombok.Getter;
import lombok.ToString;

/**
 * 执行状态
 *
 * @author wenpan 2022/09/18 12:47
 */
@Getter
@ToString
public enum ExecutionStatus {
    /**
     * 执行失败
     */
    FAILED("500", "执行失败"),
    /**
     * 执行成功
     */
    SUCCESS("200", "执行成功"),
    /**
     * 没有找到脚本
     */
    NO_SCRIPT("4004", "没有找到groovy脚本"),
    /**
     * 参数有误
     */
    PARAM_ERROR("3003", "没有找到groovy脚本");

    /**
     * 编码
     */
    private String code;
    /**
     * 含义
     */
    private String meaning;

    ExecutionStatus(String code, String meaning) {
        this.code = code;
        this.meaning = meaning;
    }
}
