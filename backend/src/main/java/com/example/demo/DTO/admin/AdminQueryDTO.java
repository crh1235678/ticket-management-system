package com.example.demo.DTO.admin;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminQueryDTO {
    private int pageNum = 1;
    private int pageSize = 10;

    private  String name;
    private  String telephone;

}
