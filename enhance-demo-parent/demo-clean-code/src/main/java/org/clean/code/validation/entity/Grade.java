package org.clean.code.validation.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 成绩
 *
 * @author Mr_wenpan@163.com 2022/05/03 15:23
 */
@Data
public class Grade {

    /**
     * 班级号
     */
    @NotBlank
    private String classNo;

}
