package com.example.demo.common.util;

import cn.dev33.satoken.stp.StpUtil;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.ResultCode;


public class SaTokenUtil {

    private static final String USER_PREFIX = "user_";
    private static final String ADMIN_PREFIX = "admin_";

    /**
     * 获取当前登录用户ID（去除前缀）
     */
    public static Long getLoginUserId() {
        String loginId = StpUtil.getLoginIdAsString();
        if (loginId.startsWith(USER_PREFIX)) {
            return Long.parseLong(loginId.replace(USER_PREFIX, ""));
        } else {
            throw new BussinessException(ResultCode.NO_PERMISSION, "当前不是用户身份");
        }
    }

    public static Long getLoginAdminId() {
        String loginId = StpUtil.getLoginIdAsString();
        if (loginId.startsWith("admin_")) {
            return Long.parseLong(loginId.replace(ADMIN_PREFIX, ""));
        } else {
            throw new BussinessException(ResultCode.NO_PERMISSION, "当前不是管理员身份");
        }
    }
}
