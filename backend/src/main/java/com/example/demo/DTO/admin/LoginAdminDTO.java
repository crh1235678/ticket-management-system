package com.example.demo.DTO.admin;

import lombok.Data;

@Data
public class LoginAdminDTO {
    private String username;

    private String userpassword;

    private String captchaId;

    private String captchaCode;
}
