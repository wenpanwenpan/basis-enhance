package org.clean.code.jackson.dto;

import lombok.Data;

import java.util.Date;

/**
 * 基础VO
 *
 * @author Mr_wenpan@163.com 2022/05/04 16:32
 */
@Data
public class BaseDTO<T> {
    private Date currentDate;
    private T other;
}
