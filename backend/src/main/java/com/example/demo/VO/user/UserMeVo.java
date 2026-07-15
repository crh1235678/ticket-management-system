package com.example.demo.VO.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserMeVo {
    private  Long id;
    private  String headurl;
    private  String username;
    private  String userpassword;
    private  String name;
    private  String sex;
    private  String telephone;
    private String email;
    private BigDecimal account;
    private  String introduce;
}
