package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication  //总开关
@MapperScan("com.example.demo.mapper")  //MPlus配置要求,告诉 Spring：去哪里找 Mapper 接口，并把它们交给 Spring 管理
public class DemoApplication {
    //程序入口
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
