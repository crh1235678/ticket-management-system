package com.example.demo.DTO.order;

import com.example.demo.common.enums.OrderStatus;
import com.example.demo.common.enums.TransportType;
import lombok.Data;
import org.springframework.boot.rsocket.server.RSocketServer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ChangeTicketDTO {

    private int pageNum = 1;
    private int pageSize = 10;
    private Long id;

    private TransportType transportType;
    private Integer seatCount;
    private String Number;
    private String origin;
    private String destination;
    private LocalDate departureTime;
    private LocalDate arrivalTime;
    private BigDecimal totalPrice;
}
