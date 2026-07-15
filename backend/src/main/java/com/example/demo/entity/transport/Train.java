package com.example.demo.entity.transport;

import com.example.demo.mapper.TrainMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import lombok.Data;

@Data
public class Train extends BaseTransport{



    @Schema(description = "动车编号")
    private String trainNumber;

    @Schema(description = "动车名称")
    private String trainName;

}
