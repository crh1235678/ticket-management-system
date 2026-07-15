package com.example.demo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//静态资源映射
@Configuration
public class FileConfig implements WebMvcConfigurer{
    /**
     * fileDir = E:\vaeee\file
     * path = upload
     */
    @Value("${file.server.dir}")
    private String fileDir = "";
    @Value("${file.server.path}")
    private String path = "";

    /**
     * 浏览器请求 /upload/xxx.png
     *         ↓
     * 匹配到 addResourceHandler("/upload/**")
     *         ↓
     * 去 file:E:\vaeee\file 目录查找
     *         ↓
     * 找到文件
     *         ↓
     * 返回给浏览器
     * @param registry
     */
    @Override
    //没有返回对象给业务逻辑注入所以不用@Bean。
    //addResourceHandlers（添加静态资源映射）
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/"+path+"/**").addResourceLocations("file:" + fileDir);
    }
}


