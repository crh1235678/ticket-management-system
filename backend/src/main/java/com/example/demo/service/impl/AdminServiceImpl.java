package com.example.demo.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.DTO.admin.AddAdminDTO;
import com.example.demo.DTO.admin.AdminQueryDTO;
import com.example.demo.DTO.admin.LoginAdminDTO;
import com.example.demo.DTO.user.AddUserDTO;
import com.example.demo.DTO.user.LoginDTO;
import com.example.demo.DTO.user.UserQueryDTO;
import com.example.demo.VO.admin.AdminVO;
import com.example.demo.VO.admin.LoginAdminVO;
import com.example.demo.VO.user.LoginUserVO;
import com.example.demo.VO.user.UserVO;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.ResultCode;
import com.example.demo.common.util.CaptchaCache;
import com.example.demo.entity.Admin;
import com.example.demo.entity.User;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


//把这个类交给spring去管理，让接口可以调用这个类
//Spring 做了什么？
//1️⃣ 扫描到 @Service
//2️⃣ 创建 AdminServiceImpl 对象
//3️⃣ 找到 AdminService 接口
//4️⃣ 自动注入进 Controller
@Service
//ServiceImpl 会自动使用 AdminMapper
//ServiceImpl 会操作 Admin 表
//implements AdminService表示：该类是 AdminService 接口的实现
//继承MyBatis-Plus 提供的 ServiceImpl 类，实现了对 Admin 实体的通用业务操作，并完成AdminService里的具体操作
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {



    //--------------------------------------管理员控制的端口--------------------------------------------------------------------------

    @Resource
    private AdminMapper adminMapper;
    @Resource
    private CaptchaCache captchaCache;

    @Override
    @Transactional
    public void addAdmin(@RequestBody AddAdminDTO dto) {
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(
                Admin::getUsername,
                dto.getUsername()
        );
        long count = this.count(lambdaQueryWrapper);
        if (count > 0){
            throw new BussinessException(ResultCode.ADMINNAME_EXIST.name());
        }
        Admin admin = new Admin();
        BeanUtils.copyProperties(dto, admin);
        this.save(admin);
    }

    @Override
    public IPage<AdminVO> listAdmins(AdminQueryDTO dto) {
        // 分页采用MP 的分页插件
        // 创建分页对象
        Page<Admin> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        //创建了一个空的查询条件对象
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        //模糊查询
        wrapper.like(
                StringUtils.isNotBlank(dto.getName()),
                Admin::getName, //告诉 MyBatis-Plus “我要操作的是 Admin 实体里的 name 这个字段”，告诉框架“是哪个字段
                dto.getName() //告诉 MyBatis-Plus “我要操作的是 Admin 实体里的 name 这个字段”，告诉框架“是哪个字段
        );
        wrapper.like(
                StringUtils.isNotBlank(dto.getTelephone()),
                Admin::getTelephone,
                dto.getTelephone()
        );
        //查询对象时用id进行倒序排序显示
        wrapper.orderByAsc(Admin::getId);
        Page<Admin> adminPage = adminMapper.selectPage(page, wrapper);
        // 把查询到的Admin 转为 AdminVO 并存储到voList里
        List<AdminVO> voList = adminPage.getRecords().stream().map(admin -> {
            AdminVO vo = new AdminVO();
            BeanUtils.copyProperties(admin, vo);
            return vo;
        }).toList();
        // 重新设置分页信息包括具体数据、总数、当前页码、每页数量
        Page<AdminVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(adminPage.getTotal());
        voPage.setCurrent(adminPage.getCurrent());
        voPage.setSize(adminPage.getSize());

        return voPage;
    }

    @Override
    public LoginAdminVO login(LoginAdminDTO dto) {
        /**
         * 1. 验证验证码
         * 2. 验证用户名密码
         * 3. SA-TOKEN登陆，获取Token
         */

        /**
         * isBlank(String str) 的定义
         * 判断字符串是否“空白”
         * 它返回 true 的情况包括：
         * 1️ null
         * 2️⃣ ""（空字符串）
         * 3️⃣ " "（一个或多个空格）
         * 4️⃣ "\t"、\n、\r（制表、换行等空白字符）
         */
        if (StringUtils.isBlank(dto.getCaptchaId())
                || StringUtils.isBlank(dto.getCaptchaCode())) {
            throw new BussinessException(ResultCode.CAPTCHA_ERROR);
        }

        boolean flag = captchaCache.validateCaptcha(
                dto.getCaptchaId(),
                dto.getCaptchaCode()
        );

        if (!flag) {
            throw new BussinessException(ResultCode.CAPTCHA_ERROR);
        }

        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, dto.getUsername());
        wrapper.eq(Admin::getUserpassword, dto.getUserpassword());

        /**
         * 根据查询条件，查询一个对象赋值给admin1。getOne()单条查询方法
         */
        Admin admin = this.getOne(wrapper);

        if (admin == null) {
            throw new BussinessException(ResultCode.ADMINNAME_PASSWORD_ERROR);
        }

        /**
         * 告诉 Sa-Token：这个 id 对应的用户，现在登录成功了,并将token和admin_id进行绑定映射
         */
        // 添加前缀，区分 用户和管理员，防止用户和管理员id冲突
        String loginId = "admin_" + admin.getId();
        StpUtil.login(loginId);
        LoginAdminVO loginVo = new LoginAdminVO();
        BeanUtils.copyProperties(admin, loginVo);
        loginVo.setToken(StpUtil.getTokenValue());

        return loginVo;
    }

}
