//统一返回结果
package com.example.demo.common.result;

import lombok.Getter;

@Getter
//<T>让 Result 能装“任意类型的数据”，并且在编译期保证数据类型是正确的。<T> 是为了“data 的类型不同”
public class Result<T> {

    //T 的作用：让编译器知道 “这里的类型是动态指定的”，从而在编译时做类型检查，避免运行时错误；
    //泛型类型数据，自定义数据类型
    private T data;
    //状态码
    private Integer code;
    //提示信息
    private String message;

    //构造类
    private Result(Integer code){
        this.code = code;
    }

    private Result(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    private Result(Integer code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //✔ 表示成功✔ 没有数据✔ 默认 code=200
    //声明一个类型 T，然后返回一个使用该类型 T 的 Result 对象。
    public static <T> Result<T> success(){
        return new Result<>(ResultCode.SUCCESS.getCode());
    }
    //✔自定义提示信息
    public static <T> Result<T> success(String message){
        return new Result<>(ResultCode.SUCCESS.getCode(), message);
    }
    //返回数据 + 成功信息
    public static <T> Result<T> data(T data){
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> error(){
        return new Result<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage());
    }
    public static <T> Result<T> error(String message){
        return new Result<>(ResultCode.ERROR.getCode(), message);
    }
    public static <T> Result<T> error(Integer code, String message){
        return new Result<>(code, message);
    }
    public static <T> Result<T> error(ResultCode resultCode){
        return new Result<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage());
    }

}
