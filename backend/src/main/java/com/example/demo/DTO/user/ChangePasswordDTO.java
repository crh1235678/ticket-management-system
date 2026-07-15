package com.example.demo.DTO.user;

import lombok.Data;

@Data
public class ChangePasswordDTO {

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;
}
