package com.example.demo.entity.transport;

import com.baomidou.mybatisplus.annotation.*;
import com.example.demo.mapper.FlightMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import lombok.Data;


@Data
@TableName("flight")
public class Flight extends BaseTransport{

    @Schema(description = "航班编号")
    private String flightNumber;

    @Schema(description = "航班名称")
    private String flightName;

    @Schema(description = "logo")
    private String logourl;


}
