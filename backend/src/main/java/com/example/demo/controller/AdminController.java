package com.example.demo.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.DTO.admin.AddAdminDTO;
import com.example.demo.DTO.admin.AdminQueryDTO;
import com.example.demo.DTO.admin.LoginAdminDTO;
import com.example.demo.DTO.user.LoginDTO;
import com.example.demo.DTO.user.UserQueryDTO;
import com.example.demo.VO.admin.AdminVO;
import com.example.demo.VO.admin.LoginAdminVO;
import com.example.demo.VO.user.LoginUserVO;
import com.example.demo.VO.user.UserVO;
import com.example.demo.entity.Admin;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.Result;
import com.example.demo.common.result.ResultCode;
import com.example.demo.service.AdminService;
import com.example.demo.common.util.CaptchaCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// 使该类在后续处理HTTP请求好后返回HTTP响应给前端
@RestController
@Tag(name="管理员信息管理")   //swagger用的
public class AdminController {

    //注入声明,AdminService 是由 Spring 管理的 Bean
    @Resource
    //声明一个 AdminService 类型的引用变量，由 Spring 注入其实现类 AdminServiceImpl 的实例
    private AdminService adminService;
    @Resource
    private CaptchaCache captchaCache;


    //声明一个 POST 请求接口
    //前端用post方法访问这个路径，后端就会把发送来JSON 数据，将其自动转换为 Admin 对象，保存到数据库，并返回新数据的 id。
    //详细解释一下啊@RequestBody Admin admin这个
    //Spring 做了这几步事：
    //从 HTTP 请求体中读取 JSON
    //使用 Jackson（JSON 解析器）
    //按字段名对应
    //自动封装成 Admin 对象
    //Admin admin = new Admin();
    //admin.setUsername("admin");
    //admin.setPassword("123456");
    //admin.setEmail("admin@test.com");
    //定义一个对外开放的方法，用来接收前端 POST 请求体中的 JSON 数据，将其解析成 Admin 对象，并返回一个 long 类型的结果。
    @PostMapping("/admin/add")
    @Operation(summary = "增加管理员")  //swagger用的
    @SaCheckRole("admin")
    //controller层都没有Result类对象为什么能返回，因为Result在success的方法中创立了
    public Result<String> add(@RequestBody AddAdminDTO dto){
        adminService.addAdmin(dto);
        return Result.success();
    }


    //当前端用 Post 请求 访问
    // / admin/list
    //Spring 就会调用 list() 方法
    @GetMapping("/admin/list")
    @Operation(summary = "列出管理员")  //swagger用的
    @SaCheckRole("admin")
    //@CrossOrigin   //跨域处理让前端能够访问到这个路径,允许前端跨域访问该路径下的方法
    //明确告诉 Java：我返回的是一个“data 为 List<Admin> 的 Result”
    public Result<IPage<AdminVO>> list(AdminQueryDTO dto){
        IPage<AdminVO> result = adminService.listAdmins(dto);
        return Result.data(result);
    }


    //根据Id来修改数据
    @PostMapping("/admin/update")
    @Operation(summary = "更新管理员信息")  //swagger用的
    @SaCheckRole("admin")
    //这里Result为什么不用告诉JAVA类型，因为这个接口“不返回业务数据”，Result 里没有 data，
    //所以根本不需要关心 T 是什么类型。
    public Result<String> update(@RequestBody Admin admin){
        adminService.updateById(admin);
        return Result.success();
    }

    //根据Id来修改数据
    //@RequestParam从请求参数中取值，并绑定到方法参数上
    @PostMapping("/admin/del")
    @Operation(summary = "删除管理员")  //swagger用的
    @SaCheckRole("admin")
    //这里接收id的集合参数，是为了前端批量删除的业务操作
    public Result<String> del(@RequestParam List<Long> ids){
        adminService.removeByIds(ids);
        return Result.success();
    }

    //管理员登陆
    @PostMapping("/admin/login")
    @Operation(summary = "管理员登陆")  //swagger用的
    //controller层都没有Result类对象为什么能返回，因为Result在success的方法中创立了
    public Result<LoginAdminVO> login(@RequestBody LoginAdminDTO dto){

        LoginAdminVO admin = adminService.login(dto);
        return Result.data(admin);
    }

    @PostMapping("/admin/logout")
    @Operation(summary = "管理员登出")
    @SaCheckRole("admin")
    @CrossOrigin
    public Result logout(){
        StpUtil.logout();
        return Result.success();
    }
}





