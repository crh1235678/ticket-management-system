package com.example.demo.service.inner.impl;


import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.ResultCode;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.inner.UserInnerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserInnerServiceImpl implements UserInnerService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private TransactionMapper transactionMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void rechargeInner(User user, BigDecimal amount, Transaction transaction){
        // 更新用户余额
        if(userMapper.recharge(user.getId(), amount, user.getVersion()) == 0){
            throw new BussinessException(ResultCode.USER_ACCOUNT_ERROR);
        }
        transactionMapper.insert(transaction);
    }
}
