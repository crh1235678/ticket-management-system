package com.example.demo.service.inner;


import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;

import java.math.BigDecimal;

public interface UserInnerService {
    void rechargeInner(User userId, BigDecimal amount, Transaction transaction);
}
