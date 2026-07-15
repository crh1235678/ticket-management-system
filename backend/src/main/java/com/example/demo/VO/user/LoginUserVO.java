package com.example.demo.VO.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class LoginUserVO {
    private  Long id;
    private  String username;
    private  String name;
    private  String telephone;
    private  String headurl;
    private  String token;
}
