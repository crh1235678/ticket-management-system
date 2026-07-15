package com.example.demo.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.example.demo.common.enums.OrderStatus;
import com.example.demo.common.enums.TransportType;
import com.example.demo.entity.Order;
import com.example.demo.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class TestController {
    @RequestMapping(value = "/test")

    public String test(@RequestParam String name){
        return "这是一个后端23a" + name;
    }

    // 只有管理员能访问
    @SaCheckRole("admin")
    @GetMapping("/test/admin")
    public String adminTest() {
        return "我是管理员接口";
    }

    // 只有用户能访问
    @SaCheckRole("user")
    @GetMapping("/test/user")
    public String userTest() {
        return "我是用户接口";
    }

    // 测试order的创建
    @Autowired
    private OrderMapper orderMapper;
    @GetMapping("/insert")
    public String testInsert() {

        Order order = new Order();
        order.setUserId(1L);
        order.setTransportType(TransportType.FLIGHT);
        order.setTicketId(1L);
        order.setTotalPrice(new BigDecimal("100"));
        order.setSeatCount(1);
        order.setStatus(OrderStatus.PAID);

        orderMapper.insert(order);

        return "ok";
    }
}
