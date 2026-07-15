package com.example.demo.DTO.flight;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightQueryDTO {

    private int pageNum = 1;
    private int pageSize = 10;

    private String flightNumber;
    private String origin;
    private String destination;
    // 这是出发日期的开始和结束，因为前台传给前端的只有触发日期的年月日，前端吧出发日期做处理变成start 和 end带有一天的初始和结束时间
    private LocalDateTime departureStart;
    private LocalDateTime departureEnd;
}

