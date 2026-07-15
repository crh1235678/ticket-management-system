package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.common.enums.OrderStatus;
import com.example.demo.entity.Order;
import org.apache.ibatis.annotations.Update;

public interface OrderMapper extends BaseMapper<Order> {


    // CAS + version 解决并发问题和数据覆盖问题

    @Update("""
      UPDATE t_order SET status =  #{status},
      version = version + 1 WHERE id = #{id} AND  version = #{version}
      """)
    int updateStatus(Long id, OrderStatus status, int version);

    @Update("""
      UPDATE t_order SET status =  #{status},
      seat_count = #{seatCount}, version = version + 1 WHERE id = #{id} AND  version = #{version}
      """)
    int updateOrderSelective(Long id, OrderStatus status, int seatCount, int version);
}
