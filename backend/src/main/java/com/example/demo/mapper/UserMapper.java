package com.example.demo.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;


//继承 MyBatis-Plus 提供的 BaseMapper 接口，实现对 User 数据表的通用增删改查操作。
public interface UserMapper extends BaseMapper<User> {


    @Update("""
UPDATE user
set account = account - #{totalPrice}, version = version + 1
where id = #{id} and account >= #{totalPrice} AND  version = #{version}
""")
    int reduceAccount(Long id, BigDecimal totalPrice, Integer  version);

    @Update("""
UPDATE user
set account = account + #{totalPrice}, version = version + 1
where id = #{id} AND  version = #{version}
""")
    int addAccount(Long id, BigDecimal totalPrice, Integer  version);


    @Update("""
    UPDATE user
    SET account = account + #{amount}, version = version + 1
    WHERE id = #{id} AND  version = #{version}
""")
    int recharge(Long id, BigDecimal amount, Integer  version);

}
