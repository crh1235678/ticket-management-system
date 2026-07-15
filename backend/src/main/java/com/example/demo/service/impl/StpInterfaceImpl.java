package com.example.demo.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// 实现 StpInterface 接口，拓展了区分用户和管理员的权限的功能
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回当前账号的角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {

        String id = loginId.toString();

        List<String> list = new ArrayList<>();

        if(id.startsWith("admin_")){
            list.add("admin");
        } else if(id.startsWith("user_")){
            list.add("user");
        }

        return list;
    }

    /**
     * 返回当前账号的权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }
}