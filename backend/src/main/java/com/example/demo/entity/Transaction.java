package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("transaction")
@Data
@Schema(description = "交易信息实体")  //swagger用的
public class Transaction {

    @Schema(description = "ID")  //swagger用的
    @TableId(type = IdType.AUTO)  //声明这个变量是主键，并且设置类型为自增
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "类型")
    private String type;
    @Schema(description = "支付方式")
    private String payType;
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @Schema(description = "备注")
    private String remark;

    // getter & setter
}