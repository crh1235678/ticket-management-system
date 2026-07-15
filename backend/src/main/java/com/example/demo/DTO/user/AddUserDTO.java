package com.example.demo.DTO.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddUserDTO {

    private  String headurl;
    private  String username;
    private  String userpassword;
    private  String name;
    private  String sex;
    private  String telephone;
    private String email;
    private  String role;
    private BigDecimal account;
    private  String introduce;
}
