package com.example.demo.VO.order;

import com.example.demo.common.enums.TransportType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 这里的数据设置，是要服务于前端新票展示和ChangeNewTicketDTO的数据需要
@Data
public class OptionalTicketVO {
    private Long transportId;
    private TransportType transportType;
    private Integer seatCount;

    private String Number;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private Integer seatRemaining;
    private BigDecimal diffPrice;
}
