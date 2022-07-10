package org.enhance.cache.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Employee implements Serializable {
    private Integer id;
    private String lastName;
    private String email;
    //性别 1男 0女 private Integer dId;
    private Integer gender;
}