//对应数据库的user

package com.example.demo.entity;


import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("user")
@Schema(description = "用户信息实体")  //swagger用的
public class User {

    //主键ID
    //@Schema(description = "用户ID")  //swagger用的
    @TableId(type = IdType.AUTO)  //声明这个变量是主键，并且设置类型为自增
    private  Long id;

    //用户头像
    //@Schema(description = "用户头像")  //swagger用的
    private  String headurl;

    //用户账号
    //@Schema(description = "用户名")  //swagger用的
    private  String username;

    //用户密码
    //@Schema(description = "用户密码")  //swagger用的
    private  String userpassword;

    //用户名
    //@Schema(description = "用户姓名")  //swagger用的
    private  String name;

    //用户性别
    //@Schema(description = "用户性别")  //swagger用的
    private  String sex;

    //用户电话号码
    //@Schema(description = "用户电话号码")  //swagger用的
    private  String telephone;

    //用户邮箱
    //@Schema(description = "用户邮箱")
    private String email;

    //用户权限
    //@Schema(description = "用户权限")
    private  String role;

    //用户余额(在数据库已给初始值)
    //@Schema(description = "用户余额")
    private BigDecimal account;

    private  String introduce;

    @Version
    private Integer version;


}
