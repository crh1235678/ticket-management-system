package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.transport.Bus;
import org.apache.ibatis.annotations.Update;

public interface BusMapper extends BaseMapper<Bus> {
    // 使用乐观锁和CAS 扣库存做法
    @Update("""
      UPDATE bus SET seat_remaining = seat_remaining - #{count},
      version = version + 1 WHERE id = #{id} AND seat_remaining >= #{count} AND version = #{version}
      """)
    int reduceSeat(Long id, Integer count, Integer version);

    @Update("""
      UPDATE bus SET seat_remaining = seat_remaining + #{count},
      version = version + 1 WHERE id = #{id} AND seat_total >= #{count} + seat_remaining AND version = #{version}
      """)
    int addSeat(Long id, Integer count, Integer version);
}
