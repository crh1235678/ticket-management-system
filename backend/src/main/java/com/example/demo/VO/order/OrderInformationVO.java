package com.example.demo.VO.order;

import com.example.demo.common.enums.TransportType;
import lombok.Data;

@Data
public class OrderInformationVO {
    private Long orderId;
    private Long ticketId;
    private TransportType transportType;
}
