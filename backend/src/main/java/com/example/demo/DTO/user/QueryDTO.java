package com.example.demo.DTO.user;

import lombok.Data;

import java.time.LocalDate;


@Data
public class QueryDTO {

    private int pageNum = 1;
    private int pageSize = 10;

    private String status;
    private String transportType;
    private String userId;
    private String number;
    private String origin;
    private String destination;
    private LocalDate departureTime;
    private LocalDate arrivalTime;

}
