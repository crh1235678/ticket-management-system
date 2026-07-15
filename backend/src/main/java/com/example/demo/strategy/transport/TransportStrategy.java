package com.example.demo.strategy.transport;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.DTO.order.ChangeTicketDTO;
import com.example.demo.DTO.order.CreateOrderDTO;
import com.example.demo.DTO.user.QueryDTO;
import com.example.demo.VO.order.OptionalTicketVO;
import com.example.demo.VO.order.OrderUserVO;
import com.example.demo.common.enums.TransportType;
import com.example.demo.entity.Order;
import com.example.demo.entity.transport.BaseTransport;
import com.example.demo.service.cache.TransportCache;

import java.util.List;

public interface TransportStrategy {

    // 当前策略类型（FLIGHT / TRAIN / BUS）
    TransportType getType();

    // 👉 用于前台用户的订单跨表查询
    List<Long> queryIds(QueryDTO query);

    // 👉 构建缓存（避免N+1）
    void buildCache(List<Order> orders, TransportCache cache);

    // 👉 填充交通信息
    void fillTransportInfo(OrderUserVO vo, Order order, TransportCache cache);

    // 👉 列出可改签的票
    IPage<OptionalTicketVO> changeTickets(ChangeTicketDTO dto,Order order);

    // 👉 减少座位数
    Integer reduceSeat(Long id,  Integer count, Integer version);

    // 👉 恢复座位数
    Integer addSeat(Long id, Integer count, Integer version);

    // 👉 填充并保存票据信息
    Order fillAndSaveOrderInfo(Long userId, BaseTransport transport, CreateOrderDTO dto);
}
