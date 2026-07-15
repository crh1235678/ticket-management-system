package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.DTO.admin.AddAdminDTO;
import com.example.demo.DTO.admin.AdminQueryDTO;
import com.example.demo.DTO.admin.LoginAdminDTO;
import com.example.demo.VO.admin.AdminVO;
import com.example.demo.VO.admin.LoginAdminVO;
import com.example.demo.entity.Admin;
import org.springframework.web.bind.annotation.RequestBody;

//继承 MyBatis-Plus 提供的 IService 接口，实现对 Admin 实体的通用业务操作支持。
public interface AdminService extends IService<Admin> {

    void addAdmin(@RequestBody AddAdminDTO dto);
    IPage<AdminVO> listAdmins(AdminQueryDTO dto);
    LoginAdminVO login(LoginAdminDTO dto);
}
