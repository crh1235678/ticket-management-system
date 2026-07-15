package com.example.demo.DTO.user;

import lombok.Data;

@Data
public class RegisterDTO {

    private String username;

    private String userpassword;

    private String captchaId;

    private String captchaCode;
}
