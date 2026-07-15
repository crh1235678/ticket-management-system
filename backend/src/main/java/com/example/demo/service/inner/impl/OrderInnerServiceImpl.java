package com.example.demo.service.inner.impl;

import com.example.demo.DTO.order.ChangeNewTicketDTO;
import com.example.demo.DTO.order.CreateOrderDTO;
import com.example.demo.VO.order.OrderInformationVO;
import com.example.demo.common.enums.OrderStatus;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.ResultCode;
import com.example.demo.entity.Order;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.entity.transport.BaseTransport;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.inner.OrderInnerService;
import com.example.demo.strategy.transport.TransportStrategyManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


// 这里只写对数据的操作还有查数据库，尽量不要写任何的业务逻辑判断
@Service
public class OrderInnerServiceImpl implements OrderInnerService {

    @Resource
    private TransportStrategyManager strategyManager;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private TransactionMapper transactionMapper;


    /**
     * 创建订单（参与外层事务）
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Order createOrderInner(BaseTransport transport, CreateOrderDTO dto, Long userId) {

        // 原子操作扣减库存
        if (strategyManager
                .get(dto.getTransportType())
                .reduceSeat(transport.getId(), dto.getSeatCount(), transport.getVersion()) == 0) {
            throw new BussinessException(ResultCode.STOCK_NOT_ENOUGH);
        }

        // 创建订单并保存
        return strategyManager
                .get(dto.getTransportType())
                .fillAndSaveOrderInfo(userId, transport, dto);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void cancelOrderInner(Order order, BaseTransport transport) {

        if (orderMapper.updateStatus(order.getId(), OrderStatus.CANCELLED, order.getVersion()) == 0) {
            throw new BussinessException(ResultCode.ERROR, "订单更新失败，请重试");
        }

/*      并发问题
        transport.rollbackSeats(order.getSeatCount());
        transportFactory.updateTransport(order.getTransportType(), transport);*/
        // 恢复座位原子操作回滚库存
        if (strategyManager
                .get(order.getTransportType())
                .addSeat(transport.getId(), order.getSeatCount(), transport.getVersion()) == 0) {
            throw new BussinessException(ResultCode.EXCEED_TOTAL_STOCK);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void refundTicketInner(Order order, BaseTransport transport, User user) {

        if (orderMapper.updateStatus(order.getId(), OrderStatus.CANCELLED, order.getVersion()) == 0) {
            throw new BussinessException(ResultCode.ERROR, "订单更新失败，请重试");
        }
        if (strategyManager
                .get(order.getTransportType())
                .addSeat(transport.getId(), order.getSeatCount(), transport.getVersion()) == 0) {
            throw new BussinessException(ResultCode.EXCEED_TOTAL_STOCK);
        }
        // 余额回滚
        if(userMapper.addAccount(user.getId(), order.getTotalPrice(), user.getVersion()) == 0){
            throw new BussinessException(ResultCode.USER_ACCOUNT_ERROR);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void payOrderInner(Order order, User user, Transaction transaction){
        // 更新订单状态，version + 原子操作解决数据覆盖问题和并发问题
        if(orderMapper.updateStatus(order.getId(), OrderStatus.PAID, order.getVersion()) == 0){
            throw new BussinessException(ResultCode.ERROR, "订单更新失败，请重试");
        }

        // 扣减用户余额并保存,原子操作解决并发问题
        if(userMapper.reduceAccount(user.getId(), order.getTotalPrice(), user.getVersion()) == 0){
            throw new BussinessException(ResultCode.ERROR, "扣除余额失败，请重试");
        }
        // 添加交易记录
        if(transactionMapper.insert(transaction) == 0){
            throw new BussinessException(ResultCode.ERROR, "添加交易记录失败，请重试");
        }
    }


    // 这个位置还是有做业务决策，其实应该得修改
    @Override
    @Transactional
    public void changeTicketInner(
            Order oldOrder,
            BaseTransport transport,
            ChangeNewTicketDTO dto,
            CreateOrderDTO createOrderDTO,
            User user,
            BigDecimal diffPrice,
            Transaction transaction
    ) {
        if (diffPrice.compareTo(BigDecimal.ZERO) > 0) {
            // 补钱
            if (userMapper.reduceAccount(user.getId(), diffPrice, user.getVersion()) == 0) {
                throw new BussinessException(ResultCode.USER_ACCOUNT_ERROR);
            }
        } else {
            // 退款
            userMapper.addAccount(user.getId(), diffPrice.abs(), user.getVersion());
        }

        // 2. 扣新票库存
        if (strategyManager.get(dto.getTransportType())
                .reduceSeat(transport.getId(), dto.getSeatCount(), transport.getVersion()) == 0) {
            throw new BussinessException(ResultCode.STOCK_NOT_ENOUGH);
        }

        // 3. 创建新订单
        Order newOrder = strategyManager
                .get(dto.getTransportType())
                .fillAndSaveOrderInfo(user.getId(), transport, createOrderDTO);
        System.out.println("看这里" + newOrder);

        // 4. 更新订单状态
        orderMapper.updateStatus(newOrder.getId(), OrderStatus.PAID, newOrder.getVersion());

        orderMapper.updateStatus(oldOrder.getId(), OrderStatus.CANCELLED, oldOrder.getVersion());

        // 5. 回滚旧库存
        strategyManager.get(oldOrder.getTransportType())
                .addSeat(oldOrder.getTicketId(), oldOrder.getSeatCount(), transport.getVersion());

        // 6. 记录交易
        transactionMapper.insert(transaction);
    }

}
