package com.example.demo.common.util;

import jakarta.annotation.Resource;
import lombok.Data;

@Data
public class Captcha {

    //验证码id
    private String captchaId;
    //验证码
    private String captchaImage;
}
