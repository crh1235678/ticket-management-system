package com.example.demo.DTO.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeDTO {

    private BigDecimal amount;     // 充值金额

    private String payType;    // 支付方式：ALIPAY / WECHAT
}
