package com.example.demo.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {
    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        // 创建DefaultKaptcha实例
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();

        // 初始化配置属性
        Properties properties = new Properties();

        // 设置验证码图片无边框
        properties.setProperty("kaptcha.border", "no");

        // 设置验证码边框颜色
        properties.setProperty("kaptcha.border.color", "34,114,200");

        // 设置验证码图片宽度和高度
        properties.setProperty("kaptcha.image.width", "125");
        properties.setProperty("kaptcha.image.height", "40");

        // 设置验证码字符集和长度
        properties.setProperty("kaptcha.textproducer.char.string", "123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");

        // 设置验证码字体名称和大小
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Arial Narrow,Serif,Helvetica,Tahoma,Times New Roman,Verdana");
        properties.setProperty("kaptcha.textproducer.font.size", "38");

        // 设置验证码背景颜色（从白色到白色，即纯白背景）
        properties.setProperty("kaptcha.background.clear.from", "white");
        properties.setProperty("kaptcha.background.clear.to", "white");

        // 将配置属性应用到DefaultKaptcha实例
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);

        // 返回配置完成的验证码生成器
        return defaultKaptcha;
    }

}
