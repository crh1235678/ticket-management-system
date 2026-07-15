package com.example.demo.VO.flight;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FlightUserVO {
    private Long id;
    private String flightNumber;
    private String flightName;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private Integer seatRemaining;
    private String logourl;
}
