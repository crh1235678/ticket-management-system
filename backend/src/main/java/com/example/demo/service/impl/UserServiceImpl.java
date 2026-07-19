package com.example.demo.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.demo.DTO.user.*;
import com.example.demo.VO.article.ArticleVO;
import com.example.demo.VO.user.LoginUserVO;
import com.example.demo.VO.user.UserMeVo;
import com.example.demo.VO.user.UserVO;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.ResultCode;
import com.example.demo.common.util.RedisCaptchaCache;
import com.example.demo.common.util.SaTokenUtil;
import com.example.demo.entity.Article;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.service.inner.UserInnerService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RedisCaptchaCache captchaCache;
    @Resource
    private TransactionMapper transactionMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserInnerService userInnerService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void changePassword(ChangePasswordDTO dto) {

        if (StringUtils.isBlank(dto.getOldPassword())
                || StringUtils.isBlank(dto.getNewPassword())
                || StringUtils.isBlank(dto.getConfirmPassword())) {
            throw new BussinessException(ResultCode.PARAM_EMPTY, "必填选项不得为空");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "两次密码输入不一致");
        }

        Long userId = SaTokenUtil.getLoginUserId();
        User dbUser = getById(userId);

        if (dbUser == null) {
            throw new BussinessException(ResultCode.USER_NOT_EXIST);
        }

        // 🔥 1. 校验旧密码（必须用 matches）
        if (!passwordEncoder.matches(dto.getOldPassword(), dbUser.getUserpassword())) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "旧密码输入错误");
        }

        // 🔥 2. 新旧密码不能相同
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "新密码不能与旧密码相同");
        }

        // 🔥 3. 新密码加密
        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        dbUser.setUserpassword(encodedNewPassword);

        updateById(dbUser);

        // 🔥 4. 强制退出登录（安全做法）
        StpUtil.logout();
    }

    @Override
    public void register(RegisterDTO dto) {

        /**
         * 1. 校验验证码
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

        /**
         * 2. 校验用户名是否存在,以及密码数是否为6位以上
         */
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());

        User existUser = getOne(wrapper);

        if (existUser != null) {
            throw new BussinessException(ResultCode.USERNAME_EXIST);
        }
        if (dto.getUserpassword().length() < 6) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "密码长度不能小于6位");
        }
        /**
         * 3. DTO → Entity
         */
        User user = new User();
        user.setUsername(dto.getUsername());
        // 明文密码
        String rawPassword = dto.getUserpassword();
        // 加密
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setUserpassword(encodedPassword);

        /**
         * 4. 初始化默认数据
         */
        user.setAccount(BigDecimal.ZERO);
        user.setRole("USER");
        user.setHeadurl("upload\\defaultavatar.png");
        user.setName("用户");
        user.setEmail("无");
        user.setSex("无");
        user.setTelephone("无");
        user.setIntroduce("无");

        /**
         * 5. 保存用户
         */
        save(user);
    }

    @Override
    public LoginUserVO login(LoginDTO dto) {

        /*
          1. 验证验证码
          2. 验证用户名密码
          3. SA-TOKEN登陆，获取Token
         */

        /*
          1. 参数校验
         */
        /*
          isBlank(String str) 的定义
          判断字符串是否“空白”
          它返回 true 的情况包括：
          1️ null
          2️⃣ ""（空字符串）
          3️⃣ " "（一个或多个空格）
          4️⃣ "\t"、\n、\r（制表、换行等空白字符）
         */
        if (StringUtils.isBlank(dto.getCaptchaId())
                || StringUtils.isBlank(dto.getCaptchaCode())) {
            throw new BussinessException(ResultCode.CAPTCHA_ERROR);
        }

        /**
         * 2. 验证验证码
         */
        boolean flag = captchaCache.validateCaptcha(
                dto.getCaptchaId(),
                dto.getCaptchaCode()
        );

        if (!flag) {
            throw new BussinessException(ResultCode.CAPTCHA_ERROR);
        }

        /**
         * 3. 校验用户名密码
         */
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = this.getOne(wrapper);
        if (user == null || !passwordEncoder.matches(dto.getUserpassword(), user.getUserpassword())) {
            throw new BussinessException(ResultCode.USERNAME_PASSWORD_ERROR);
        }

        /**
         * 4. 登录（Sa-Token）
         */
        /**
         * 告诉 Sa-Token：这个 id 对应的用户，现在登录成功了
         */
        String loginId = "user_" + user.getId();
        StpUtil.login(loginId);
        LoginUserVO loginVo = new LoginUserVO();
        BeanUtils.copyProperties(user, loginVo);
        loginVo.setToken(StpUtil.getTokenValue());

        return loginVo;
    }
    @Override
    public IPage<UserVO> listUsers(UserQueryDTO dto) {
        // 分页采用MP 的分页插件
        // 创建分页对象
        Page<User> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        //创建了一个空的查询条件对象
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //模糊查询
        wrapper.like(
                StringUtils.isNotBlank(dto.getName()),
                User::getName, //告诉 MyBatis-Plus “我要操作的是 Admin 实体里的 name 这个字段”，告诉框架“是哪个字段
                dto.getName() //告诉 MyBatis-Plus “我要操作的是 Admin 实体里的 name 这个字段”，告诉框架“是哪个字段
        );
        wrapper.like(
                StringUtils.isNotBlank(dto.getTelephone()),
                User::getTelephone,
                dto.getTelephone()
        );
        //查询对象时用id进行倒序排序显示
        wrapper.orderByAsc(User::getId);
        Page<User> userPage = userMapper.selectPage(page, wrapper);
        // 把查询到的User 转为 UserVO 并存储到voList里
        List<UserVO> voList = userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).toList();
        // 重新设置分页信息包括具体数据、总数、当前页码、每页数量
        Page<UserVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(userPage.getTotal());
        voPage.setCurrent(userPage.getCurrent());
        voPage.setSize(userPage.getSize());

        return voPage;
    }


    @Override
    public void updateUser(UpdateUserDTO dto) {
        //        已经添加全局方法去处理去前缀功能
//        String loginId = StpUtil.getLoginIdAsString();
//
//        Long userId = null;
//
//        if (loginId.startsWith("user_")) {
//            userId = Long.parseLong(loginId.replace("user_", ""));
//        } else {
//            throw new BussinessException(ResultCode.NO_PERMISSION, "没有权限访问");
//        }

        Long userId = SaTokenUtil.getLoginUserId();
        User user = this.getById(userId);
        if (user == null) {
            throw new BussinessException(ResultCode.USER_NOT_EXIST);
        }
        // 👉 只更新允许的字段
        BeanUtils.copyProperties(dto, user);
        this.updateById(user);
    }

    @Override
    public UserMeVo listMe() {
        Long userId = SaTokenUtil.getLoginUserId();
        User user = this.getById(userId);
        UserMeVo userMeVo = new UserMeVo();
        BeanUtils.copyProperties(user, userMeVo);
        return  userMeVo;
    }

    @Override
    @Transactional
    public void recharge(RechargeDTO dto) {
        // 1️⃣ 获取当前登录用户ID
//        String loginId = StpUtil.getLoginIdAsString();
//
//        Long userId;
//
//        if (loginId.startsWith("user_")) {
//            userId = Long.parseLong(loginId.replace("user_", ""));
//        } else {
//            throw new BussinessException(ResultCode.NO_PERMISSION, "当前不是用户身份");
//        }
        Long userId = SaTokenUtil.getLoginUserId();
        // 2️⃣ 校验充值金额
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "充值金额必须大于0");
        }
        // 3️⃣ 校验支付方式
        if (!"alipay".equals(dto.getPayType()) && !"wechat".equals(dto.getPayType())) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "支付方式不合法");
        }
        // 4️⃣ 查询用户
        User user = getById(userId);
        if (user == null) {
            throw new BussinessException(ResultCode.USER_NOT_EXIST);
        }
        // 5️⃣ 更新用户余额
        BigDecimal newAccount = user.getAccount().add(dto.getAmount());
        user.setAccount(newAccount);

        // 6️⃣ 写入充值流水
        // 创建充值流水数据体，把数据写入数据库
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(dto.getAmount());
        transaction.setType("RECHARGE");
        transaction.setPayType(dto.getPayType());

        userInnerService.rechargeInner(user, dto.getAmount(), transaction);
    }

    @Override
    @Transactional
    public void addUser(@RequestBody AddUserDTO dto) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //等价于WHERE username = #{admin.username}
        lambdaQueryWrapper.eq(
                User::getUsername,
                dto.getUsername()
        );
        //拿查询条件去数据库查,有多少条记录符合
        //实际效果SELECT COUNT(*) FROM admin WHERE username = ?
        long count = this.count(lambdaQueryWrapper);
        if (count > 0){
            /**中断当前方法
             不再执行 save
             直接跳到 @RestControllerAdvice
             寻找这个注释@ExceptionHandler(BussinessException.class)，调用对应方法
             */
            throw new BussinessException(ResultCode.USERNAME_EXIST);
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        this.save(user);
    }

}
