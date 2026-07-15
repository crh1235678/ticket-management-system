package com.example.demo.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.example.demo.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//对RestController或者Controller抛出的异常进行统一处理
//拦截 Controller层抛出的异常，返回值会直接转成JSON格式以便给前端
@RestControllerAdvice
public class ControllerExceptionHandler {

    //工作机理:controller 出现异常后，异常首先由 Spring 框架捕获，
    //Spring 根据 @RestControllerAdvice 中定义的
    //@ExceptionHandler(Exception.class) 方法，
    //将异常对象注入为 Exception e 参数，并调用对应方法进行处理
    //❌ 不是 @RestControlleAdvice 主动去抓异常
    //✅ 是 Spring 抓异常，然后“问 @RestControlleAdvice 怎么处理”
    @ExceptionHandler(Exception.class)
    //Exception e 是 Spring 在运行时捕获到异常对象后，自动注入给 @ExceptionHandler 方法的参数。
    public Result handleException(Exception e){
        return Result.error(e.getMessage());
    }

    //处理自定义业务异常,Exception.class是兜底处理所有异常的通用处理器
    //也就是对没有throw的异常，一律丢给Exception兜底处理
    @ExceptionHandler(BussinessException.class)
    public Result handleBussinessException(BussinessException e){
        return Result.error(e.getCode(), e.getMessage());
    }

    //这个是处理SaToken的异常
    @ExceptionHandler(NotLoginException.class)
    public Result handleNotLoginException(NotLoginException e){
        return Result.error(e.getCode(), e.getMessage());
    }

}
