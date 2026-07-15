package com.example.demo.VO.train;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TrainUserVO {
    private Long id;
    private String trainNumber;
    private String trainName;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private Integer seatRemaining;
}
