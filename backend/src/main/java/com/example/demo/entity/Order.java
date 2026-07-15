package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.demo.common.enums.OrderStatus;
import com.example.demo.common.enums.TransportType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {

    // 主键
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户ID
    @Schema(description = "用户ID")
    private Long userId;

    // 交通类型（FLIGHT / TRAIN / BUS）
    @Schema(description = "交通类型")
    private TransportType transportType;

    // 票ID
    @Schema(description = "票ID")
    private Long ticketId;

    // 价格
    @Schema(description = "总价")
    private BigDecimal totalPrice;

    // 购票数量
    @Schema(description = "购票数量")
    private Integer seatCount;

    // 订单状态（UNPAID / PAID / CANCELLED）
    @Schema(description = "订单状态")
    private OrderStatus status;

    // 创建时间
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    // 乐观锁，用来记录版本，防止超卖
    private Integer version;
}
