package org.enhance.message.spring.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 手机
 *
 * @author wenpanfeng 2022/08/03 15:26
 */
@Data
public class Phone {

    /**
     * 号码
     */
    String number;
    /**
     * 型号
     */
    String type;

    public Phone(String number, String type) {
        if (StringUtils.isBlank(number)) {
            throw new IllegalArgumentException("number can not be null.");
        }
        this.number = number;
        this.type = type;
    }
}