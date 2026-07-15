package com.example.demo.strategy.transport;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.DTO.order.ChangeTicketDTO;
import com.example.demo.DTO.order.CreateOrderDTO;
import com.example.demo.DTO.user.QueryDTO;
import com.example.demo.VO.order.OptionalTicketVO;
import com.example.demo.VO.order.OrderUserVO;
import com.example.demo.common.enums.OrderStatus;
import com.example.demo.common.enums.TransportType;
import com.example.demo.entity.Order;
import com.example.demo.entity.transport.BaseTransport;
import com.example.demo.entity.transport.Train;
import com.example.demo.mapper.TrainMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.service.TrainService;
import com.example.demo.service.cache.TransportCache;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TrainStrategy implements TransportStrategy {

    @Resource
    private TrainService trainService;
    @Resource
    private TrainMapper trainMapper;
    @Resource
    private OrderMapper orderMapper;

    @Override
    public TransportType getType() {
        return TransportType.TRAIN;
    }

    @Override
    public IPage<OptionalTicketVO> changeTickets(ChangeTicketDTO dto,Order order){
        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(
                Train::getOrigin,
                dto.getOrigin()
        );
        wrapper.eq(
                Train::getDestination,
                dto.getDestination()
        );

        if (dto.getDepartureTime() != null) {
            LocalDate dateD = dto.getDepartureTime();
            wrapper.between(
                    Train::getDepartureTime,
                    dateD.atStartOfDay(),
                    dateD.atTime(23, 59, 59)
            );
        }
        if (dto.getArrivalTime() != null) {
            LocalDate dateA = dto.getArrivalTime();
            wrapper.between(
                    Train::getArrivalTime,
                    dateA.atStartOfDay(),
                    dateA.atTime(23, 59, 59)
            );
        }
        // 排除原票
        wrapper.ne(Train::getId, order.getTicketId());
        // 剩余座位数需要大于用户购买数
        wrapper.ge(Train::getSeatRemaining, order.getSeatCount());
        // 限制还未发车
        wrapper.gt(Train::getDepartureTime, LocalDateTime.now());
        wrapper.orderByAsc(Train::getDepartureTime);

        Page<Train> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<Train> trainIPage = trainMapper.selectPage(page, wrapper);

        System.out.println("看这里1" + trainIPage);
        return trainIPage.convert(train -> {
            OptionalTicketVO vo = new OptionalTicketVO();
            vo.setTransportType(TransportType.TRAIN);
            vo.setSeatCount(order.getSeatCount());
            vo.setTransportId(train.getId());
            vo.setNumber(train.getTrainNumber());
            vo.setOrigin(train.getOrigin());
            vo.setDestination(train.getDestination());
            vo.setDepartureTime(train.getDepartureTime());
            vo.setArrivalTime(train.getArrivalTime());
            vo.setPrice(train.getPrice());
            vo.setSeatRemaining(train.getSeatRemaining());
            BigDecimal newPrice = train.getPrice()
                    .multiply(BigDecimal.valueOf(order.getSeatCount()));
            vo.setDiffPrice(newPrice.subtract(order.getTotalPrice()));
            System.out.println("看这里2" + vo);
            return vo;
        });
    }
    @Override
    public List<Long> queryIds(QueryDTO query) {

        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();

        // 航班号
        wrapper.like(StringUtils.isNotBlank(query.getNumber()),
                Train::getTrainNumber, query.getNumber());

        // 出发地
        wrapper.like(StringUtils.isNotBlank(query.getOrigin()),
                Train::getOrigin, query.getOrigin());

        // 目的地
        wrapper.like(StringUtils.isNotBlank(query.getDestination()),
                Train::getDestination, query.getDestination());

        // =========================
        // ✅ 出发日期筛选
        // =========================
        if (query.getDepartureTime() != null) {

            LocalDate date = query.getDepartureTime();

            wrapper.between(
                    Train::getDepartureTime,
                    date.atStartOfDay(),
                    date.atTime(23, 59, 59)
            );
        }

        // =========================
        // ✅ 到达日期筛选，前端传入年月日，后端添加小时分钟和秒让其对应数据库
        // =========================
        if (query.getArrivalTime() !=  null ) {

            LocalDate date = query.getArrivalTime();

            wrapper.between(
                    Train::getArrivalTime,
                    date.atStartOfDay(),
                    date.atTime(23, 59, 59)
            );
        }

        // 查询航班ID
        return trainMapper.selectList(wrapper)
                .stream()
                .map(Train::getId)
                .collect(Collectors.toList());
    }

    // 缓存机票信息
    @Override
    public void buildCache(List<Order> orders, TransportCache cache) {

        List<Long> ids = orders.stream()
                .filter(o -> TransportType.TRAIN.equals(o.getTransportType()))
                .map(Order::getTicketId)
                .distinct()
                .toList();

        if (!ids.isEmpty()) {
            Map<Long, Train> map = trainService.listByIds(ids)
                    .stream()
                    .collect(Collectors.toMap(Train::getId, f -> f));

            cache.setTrainMap(map);
        }
    }

    // 机票信息填充返回给用户
    @Override
    public void fillTransportInfo(OrderUserVO vo, Order order, TransportCache cache) {

        Train train = cache.getTrainMap().get(order.getTicketId());

        if (train != null) {
            vo.setTicketId(train.getId());
            vo.setNumber(train.getTrainNumber());
            vo.setName(train.getTrainName());
            vo.setOrigin(train.getOrigin());
            vo.setDestination(train.getDestination());
            vo.setDepartureTime(train.getDepartureTime());
            vo.setArrivalTime(train.getArrivalTime());
        }
    }

    // 减少座位
    @Override
    public Integer reduceSeat(Long id, Integer count, Integer version) {
        return trainMapper.reduceSeat(id, count, version);
    }

    // 增加座位
    @Override
    public Integer addSeat(Long id, Integer count, Integer version) {
        return trainMapper.addSeat(id, count, version);
    }

    // 填充并保存票据信息
    @Override
    public Order fillAndSaveOrderInfo(Long userId, BaseTransport transport, CreateOrderDTO dto) {
        Order order = new Order();
        order.setUserId(userId);
        order.setTicketId(transport.getId());
        order.setTransportType(dto.getTransportType());
        order.setSeatCount(dto.getSeatCount());
        order.setTotalPrice(transport.getPrice().multiply(BigDecimal.valueOf(dto.getSeatCount())));
        order.setStatus(OrderStatus.UNPAID);
        order.setVersion(0);
        orderMapper.insert(order);
        return order;
    }
}
