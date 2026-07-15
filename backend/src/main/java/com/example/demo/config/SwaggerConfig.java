package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//声明当前类是一个“配置类”
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        //接口文档标题
                        .title("vaeee车票网站脚手架")
                        //接口文档版本号
                        .version("1.0.0")
                        //接口文档说明
                        .description("车票网站接口文档")
                        //接口文档作者信息
                        .contact(new Contact().name("vaeee").url("www.vaeee.com"))

                );
    }

}
