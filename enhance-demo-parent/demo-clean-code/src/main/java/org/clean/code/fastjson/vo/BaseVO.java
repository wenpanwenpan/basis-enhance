package org.clean.code.fastjson.vo;

import lombok.Data;

import java.util.Date;

/**
 * 基础VO
 *
 * @author Mr_wenpan@163.com 2022/05/04 16:32
 */
@Data
public class BaseVO<T> {
    private Date currentDate;
    private T other;
}
