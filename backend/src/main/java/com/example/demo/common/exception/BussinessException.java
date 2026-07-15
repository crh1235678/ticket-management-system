package com.example.demo.common.exception;

import com.example.demo.common.result.ResultCode;
import lombok.Data;


@Data
//RuntimeException是运行时异常，继承自Exception
public class BussinessException extends RuntimeException{

    //错误码
    private Integer code;
    //错误信息
    private String message;
    public BussinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public BussinessException(String message) {
        this.message = message;
    }
    public BussinessException(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BussinessException(ResultCode resultCode, String message) {
        this.code = resultCode.getCode();
        this.message = message;
    }
}
