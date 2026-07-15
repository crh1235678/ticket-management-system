package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.DTO.user.*;
import com.example.demo.VO.user.LoginUserVO;
import com.example.demo.VO.user.UserMeVo;
import com.example.demo.VO.user.UserVO;
import com.example.demo.entity.User;
import jakarta.validation.Valid;

//继承 MyBatis-Plus 提供的 IService 接口，实现对 Admin 实体的通用业务操作支持。
public interface UserService extends IService<User> {

    void changePassword(ChangePasswordDTO dto);
    void register(RegisterDTO dto);
    LoginUserVO login(LoginDTO dto);
    IPage<UserVO> listUsers(UserQueryDTO dto);
    void updateUser(UpdateUserDTO dto);
    void recharge(@Valid RechargeDTO dto);
    void addUser(AddUserDTO dto);
    UserMeVo listMe();
}
