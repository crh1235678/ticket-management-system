//ResultCode 枚举不是“判断结果”，而是“规定结果长什么样”。
package com.example.demo.common.result;


import lombok.AllArgsConstructor;
import lombok.Getter;

//自动生成一个“包含所有字段的构造方法”，也就是this.属性=xxx
@AllArgsConstructor
//自动为所有字段生成 getter 方法,就是get后返回
@Getter
public enum ResultCode {

    //根据枚举中定义的状态码，统一获取并返回与之绑定的提示信息和状态值。
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),

    CREAT_CAPTCHA_ERROR(4001, "获取验证码失败!"),
    CAPTCHA_ERROR(4002, "验证码错误!"),

    USERNAME_EXIST(1001, "用户名已存在!"),
    USERNAME_PASSWORD_ERROR(1002, "用户名或密码错误!"),
    USER_NOT_EXIST(1003, "用户不存在!"),
    USER_ACCOUNT_ERROR(1004, "余额错误!"),

    ADMINNAME_EXIST(8001, "用户名已存在!"),
    ADMINNAME_PASSWORD_ERROR(8002, "用户名或密码错误!"),

    FLIGHT_NOT_EXIST(9001, "航班不存在!"),
    TRAIN_NOT_EXIST(9002, "列车不存在!"),
    BUS_NOT_EXIST(9003, "巴士不存在!"),

    PARAM_EMPTY(2001,"参数为空！"),
    PARAM_ERROR(2002,"参数错误!"),

    NO_PERMISSION(3001, "无权限访问!"),

    ORDER_STATUS(5001, "订单状态错误!"),
    ORDER_NOT_EXIST(5002, "订单不存在!"),

    ARTICLE_NOT_EXIST(6001, "文章不存在!"),
    ARTICLE_ERROR(6002, "发生错误!"),

    STOCK_NOT_ENOUGH(7001, "库存不足!"),
    EXCEED_TOTAL_STOCK(7002, "超出库存!");


    //响应状态码
    private Integer code;

    //响应提示信息
    private String message;
}
