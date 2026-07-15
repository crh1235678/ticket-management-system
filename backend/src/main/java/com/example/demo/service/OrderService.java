package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.DTO.order.ChangeTicketDTO;
import com.example.demo.DTO.order.CreateOrderDTO;
import com.example.demo.DTO.order.ModifyOrderDTO;
import com.example.demo.DTO.user.QueryDTO;
import com.example.demo.DTO.order.ChangeNewTicketDTO;
import com.example.demo.VO.order.OptionalTicketVO;
import com.example.demo.VO.order.OrderInformationVO;
import com.example.demo.VO.order.OrderUserVO;
import com.example.demo.entity.Order;
import jakarta.validation.Valid;

public interface OrderService extends IService<Order> {

    OrderInformationVO createOrder(@Valid CreateOrderDTO dto);

    void cancelOrder(Long orderId);

    void refundTicket(Long orderId);

    void payOrder(Long orderId);

    IPage<OrderUserVO> queryPage(QueryDTO query, Long userId, boolean isAdmin);

    void modifyOrder (@Valid ModifyOrderDTO dto);

    IPage<OptionalTicketVO> changeTicketList(ChangeTicketDTO dto);

    void changeConfirm(ChangeNewTicketDTO dto);
}
