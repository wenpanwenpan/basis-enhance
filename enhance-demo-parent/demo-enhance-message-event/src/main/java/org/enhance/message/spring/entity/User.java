package org.enhance.message.spring.entity;

import lombok.Data;

/**
 * 用户Entity
 *
 * @author wenpanfeng 2022/08/02 17:40
 */
@Data
public class User {
    private String name;
    private int age;
    private String address;
}