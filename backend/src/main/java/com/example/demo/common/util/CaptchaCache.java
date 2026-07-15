package com.example.demo.common.util;

import org.springframework.stereotype.Component;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class CaptchaCache {

    //验证码缓存
    //因为会有多个线程访问这个缓存，所以使用 ConcurrentHashMap（并发安全哈希表）
    private static ConcurrentHashMap<String, String> captchaMap = new ConcurrentHashMap<>();

    //存储验证码
    public void storeCaptcha(String captchaId, String captcha) {
        captchaMap.put(captchaId, captcha);
    }

    //移除验证码
    public void removeCaptcha(String captchaId) {
        captchaMap.remove(captchaId);
    }

    //检查验证码
    public Boolean validateCaptcha(String captchaId, String captcha) {
        //获取存储的验证码
        String storedcaptcha = captchaMap.get(captchaId);
        if (storedcaptcha != null && storedcaptcha.equals(captcha)) {
            //验证码正确，移除验证码
            removeCaptcha(captchaId);
            return true;
        }
        return false;
    }

}
