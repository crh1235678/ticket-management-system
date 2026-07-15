package com.example.demo.DTO.order;

import com.example.demo.common.enums.TransportType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDTO {

    // 这里我都是使用了注解校验，之前都是手动校验在impl中实现，他们的区别就是手动可以写复杂校验逻辑，而注解是简单的校验逻辑
    @NotNull(message = "票ID不能为空")
    private Long transportId;

    @NotBlank(message = "票类型不能为空")
    private TransportType transportType; // 使用枚举类型，限制输入

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    private Integer seatCount;

}
