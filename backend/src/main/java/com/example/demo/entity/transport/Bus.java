package com.example.demo.entity.transport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Bus extends BaseTransport{


    @Schema(description = "巴士编号")
    private String busNumber;

    @Schema(description = "巴士名称")
    private String busName;

}
