package com.example.demo.VO.bus;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BusVO {
    private Long id;
    private String busNumber;
    private String busName;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private Integer seatTotal;
    private Integer seatRemaining;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
