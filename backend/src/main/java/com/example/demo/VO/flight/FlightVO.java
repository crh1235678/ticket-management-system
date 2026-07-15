package com.example.demo.VO.flight;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FlightVO {
    private Long id;
    private String flightNumber;
    private String flightName;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private Integer seatTotal;
    private Integer seatRemaining;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String logourl;
}
