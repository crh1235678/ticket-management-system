package com.example.demo.entity.transport;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public abstract class BaseTransport {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private Integer seatTotal;
    private Integer seatRemaining;
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    // version用来记录版本，防止数据覆盖
    private Integer version;

    // 公共方法
    // 预定座位
/*    public void bookTicket(int seats) {
        if (seatRemaining >= seats) {
            seatRemaining -= seats;
        } else {
            throw new RuntimeException("座位不足");
        }
    }*/

    // 回滚处理
    public void rollbackSeats(int count) {
        if (count <= 0) return;
        this.seatRemaining += count;
        // 可以加一个最大库存判断
//        if (this.seatRemaining > this.seatTotal) {
//            this.seatRemaining = this.seatTotal;
//        }
    }

}
