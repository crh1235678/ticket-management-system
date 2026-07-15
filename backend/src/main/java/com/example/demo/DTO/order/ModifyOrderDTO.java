package com.example.demo.DTO.order;

import com.example.demo.common.enums.OrderStatus;
import lombok.Data;

@Data
public class ModifyOrderDTO {
    private Long id;
    private OrderStatus status;
    private Integer seatCount;
}
