package com.example.demo.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Admin;


//继承 MyBatis-Plus 提供的 BaseMapper 接口，实现对 Admin 数据表的通用增删改查操作。
public interface AdminMapper extends BaseMapper<Admin> {
}
