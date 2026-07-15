package com.example.demo.VO.order;

import com.example.demo.common.enums.OrderStatus;
import com.example.demo.common.enums.TransportType;
import lombok.Data;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.time.LocalDateTime;

@Data
public class OrderUserVO {

    private Long id;
    private Long userId;
    private String username;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private TransportType transportType;
    private Long ticketId;
    private String Number;
    private String Name;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer seatCount;
    private BigDecimal totalPrice;
    private OrderStatus status;
}
