package com.example.demo.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.DTO.user.*;
import com.example.demo.VO.user.LoginUserVO;
import com.example.demo.VO.user.UserMeVo;
import com.example.demo.VO.user.UserVO;
import com.example.demo.common.util.SaTokenUtil;
import com.example.demo.entity.User;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.Result;
import com.example.demo.common.result.ResultCode;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 使该类在后续处理HTTP请求好后返回HTTP响应给前端
@RestController
@Tag(name="用户信息管理")   //swagger用的
public class
UserController {
    //注入声明,UserService 是由 Spring 管理的 Bean
    @Resource
    //声明一个 UserService 类型的引用变量，由 Spring 注入其实现类 AdminServiceImpl 的实例
    private UserService userService;

    //根据Id来修改数据
    @PostMapping("/user/update")
    //@SaCheckRole("user") //检测用户token是否存在
    //这里Result为什么不用告诉JAVA类型，因为这个接口“不返回业务数据”，Result 里没有 data，
    //所以根本不需要关心 T 是什么类型。
    public Result<String> update(@RequestBody UpdateUserDTO dto){

        userService.updateUser(dto);

        return Result.success();
    }

//    //这一段问题是用户可以修改传入到的id来看到其他用户信息，所以这里要进行修改
//    @GetMapping("/user/getById")
//    @Operation(summary = "根据ID查询用户")
//    @SaCheckLogin   //接口使用，token认证。他会抛出SATOKEN异常给exception处理
//    @CrossOrigin
//    public Result<User> getById(@RequestParam Integer id){
//        User user = userService.getById(id);
//        return Result.data(user);
//    }

    // 修改后的id查询
    @GetMapping("/user/me")
    @Operation(summary = "根据ID查询用户")
    @SaCheckRole("user") //检测用户token是否存在
    public Result<UserMeVo> getCurrentUser(){
        UserMeVo userMeVo = userService.listMe();
        return Result.data(userMeVo);
    }

    //用户登陆
    @PostMapping("/user/login")
    @Operation(summary = "用户登陆")   //swagger用的
    //controller层都没有Result类对象为什么能返回，因为Result在success的方法中创立了
    public Result<LoginUserVO> login(@RequestBody LoginDTO dto) {

        LoginUserVO  user = userService.login(dto);

        return Result.data(user);
    }

    @PostMapping("/user/register")
    @Operation(summary = "用户注册")
    public Result<String> register(@RequestBody RegisterDTO dto) {

        userService.register(dto);

        return Result.success("注册成功");
    }

    @PostMapping("/user/changePassword")
    @Operation(summary = "修改密码")
    @SaCheckRole("user") //检测用户token是否存在
    public Result<String> changePassword(@RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return Result.success("密码修改成功，请重新登录");
    }

    // 用户充值
    @PostMapping("/user/recharge")
    @Operation(summary = "用户充值")
    @SaCheckRole("user")
    public Result<String> recharge(@Valid @RequestBody RechargeDTO dto) {
        userService.recharge(dto);
        return Result.success("充值成功");
    }

    @PostMapping("/user/logout")
    @Operation(summary = "用户登出")
    @SaCheckRole("user")
    public Result<String> logout(){
        StpUtil.logout();
        return Result.success();
    }

    //--------------------------------------管理员控制的端口---------------------------------------------------------------------------
    //以下是要修改放到管理员controller里的，但是有个问题Controller 的区分核心原则是“按实体表或业务模块来划分”，而不是按谁来访问
    //因为正常来说是通过SATOKEN进行权限认证的，例如@SaCheckRole("admin")就是管理员权限，只有管理员可以访问
    //--------------------------------------已经修改完毕----------------------------------------------------------------------


    //管理员添加用户
    @PostMapping("/user/add")
    //@Operation(summary = "增加用户")  //swagger用的
    @SaCheckRole("admin")
    public Result<String> add(@RequestBody AddUserDTO dto){
        userService.addUser(dto);
        return Result.success();
    }

    //删除用户
    //@RequestParam从请求参数中取值，并绑定到方法参数上
    @PostMapping("/user/del")
    @SaCheckRole("admin")
    @CrossOrigin
    //这里接收id的集合参数，是为了前端批量删除的业务操作
    public Result<String> del(@RequestParam List<Long> ids){
        userService.removeByIds(ids);
        return Result.success();
    }


    @GetMapping("/user/list")
    @Operation(summary = "列出用户") //swagger用的
    @SaCheckRole("admin")
    public Result<IPage<UserVO>> list(UserQueryDTO dto){
        IPage<UserVO> result = userService.listUsers(dto);
        return Result.data(result);
    }
}
