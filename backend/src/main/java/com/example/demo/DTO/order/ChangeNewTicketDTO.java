package com.example.demo.DTO.order;

import com.example.demo.common.enums.TransportType;
import lombok.Data;

@Data
public class ChangeNewTicketDTO {

    private Long oldId;
    private Long transportId;
    private TransportType transportType;
    private Integer seatCount;

}
