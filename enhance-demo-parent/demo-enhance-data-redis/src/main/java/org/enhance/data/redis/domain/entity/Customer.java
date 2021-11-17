package org.enhance.data.redis.domain.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员表
 *
 * @author Mr_wenpan@163.com 2021/11/17 10:27
 */
@Data
public class Customer implements Serializable {

    private Long customerId;

    private String customerName;

    private String mobileNumber;

    private String nickName;

    private Integer blackListFlag;

    private String remark;

    private String email;
}
