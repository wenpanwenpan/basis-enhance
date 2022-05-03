package org.enhance.core.test.mockito.common;

import lombok.Data;

import java.util.Date;

/**
 * 账户信息
 *
 * @author Mr_wenpan@163.com 2022/04/17 18:34
 */
@Data
public class Account {
    private String username;
    private String password;
    private Date registDate;
}
