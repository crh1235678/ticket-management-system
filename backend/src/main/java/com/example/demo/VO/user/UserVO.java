package com.example.demo.VO.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserVO {
    private  Long id;
    private  String headurl;
    private  String username;
    private  String userpassword;
    private  String name;
    private  String sex;
    private  String telephone;
    private String email;
    private  String role;
    private BigDecimal account;
}
