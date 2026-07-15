package com.example.demo.service.inner;

import com.example.demo.DTO.order.ChangeNewTicketDTO;
import com.example.demo.DTO.order.CreateOrderDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.entity.transport.BaseTransport;

import java.math.BigDecimal;

public interface OrderInnerService {

    Order createOrderInner(BaseTransport transport, CreateOrderDTO dto, Long userId);
    void cancelOrderInner(Order order, BaseTransport transport);
    void refundTicketInner(Order order, BaseTransport transport, User user);
    void payOrderInner(Order order, User user, Transaction transaction);
    void changeTicketInner(
            Order oldOrder,
            BaseTransport transport,
            ChangeNewTicketDTO dto,
            CreateOrderDTO createOrderDTO,
            User user,
            BigDecimal diffPrice,
            Transaction transaction
    );
}
