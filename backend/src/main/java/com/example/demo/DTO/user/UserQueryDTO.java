package com.example.demo.DTO.user;


import lombok.Data;

@Data
public class UserQueryDTO {

    private String name;

    private String telephone;

    private Integer pageNum;

    private Integer pageSize;
}
