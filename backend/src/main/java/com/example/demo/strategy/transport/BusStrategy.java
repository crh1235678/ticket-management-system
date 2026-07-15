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
import com.example.demo.entity.transport.Bus;
import com.example.demo.mapper.BusMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.service.BusService;
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
public class BusStrategy implements TransportStrategy {

    @Resource
    private BusService busService;
    @Resource
    private BusMapper busMapper;
    @Resource
    private OrderMapper orderMapper;

    @Override
    public TransportType getType() {
        return TransportType.BUS;
    }

    @Override
    public IPage<OptionalTicketVO> changeTickets(ChangeTicketDTO dto,Order order){
        LambdaQueryWrapper<Bus> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(
                Bus::getOrigin,
                dto.getOrigin()
        );
        wrapper.eq(
                Bus::getDestination,
                dto.getDestination()
        );

        if (dto.getDepartureTime() != null) {
            LocalDate dateD = dto.getDepartureTime();
            wrapper.between(
                    Bus::getDepartureTime,
                    dateD.atStartOfDay(),
                    dateD.atTime(23, 59, 59)
            );
        }
        if (dto.getArrivalTime() != null) {
            LocalDate dateA = dto.getArrivalTime();
            wrapper.between(
                    Bus::getArrivalTime,
                    dateA.atStartOfDay(),
                    dateA.atTime(23, 59, 59)
            );
        }
        // 排除原票
        wrapper.ne(Bus::getId, order.getTicketId());
        // 剩余座位数需要大于用户购买数
        wrapper.ge(Bus::getSeatRemaining, order.getSeatCount());
        // 限制还未发车
        wrapper.gt(Bus::getDepartureTime, LocalDateTime.now());
        wrapper.orderByAsc(Bus::getDepartureTime);

        Page<Bus> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<Bus> busIPage = busMapper.selectPage(page, wrapper);

        System.out.println("看这里1" + busIPage);
        return busIPage.convert(bus -> {
            OptionalTicketVO vo = new OptionalTicketVO();
            vo.setTransportType(TransportType.BUS);
            vo.setSeatCount(order.getSeatCount());
            vo.setTransportId(bus.getId());
            vo.setNumber(bus.getBusNumber());
            vo.setOrigin(bus.getOrigin());
            vo.setDestination(bus.getDestination());
            vo.setDepartureTime(bus.getDepartureTime());
            vo.setArrivalTime(bus.getArrivalTime());
            vo.setPrice(bus.getPrice());
            vo.setSeatRemaining(bus.getSeatRemaining());
            BigDecimal newPrice = bus.getPrice()
                    .multiply(BigDecimal.valueOf(order.getSeatCount()));
            vo.setDiffPrice(newPrice.subtract(order.getTotalPrice()));
            System.out.println("看这里2" + vo);
            return vo;
        });
    }
    @Override
    public List<Long> queryIds(QueryDTO query) {

        LambdaQueryWrapper<Bus> wrapper = new LambdaQueryWrapper<>();

        // 航班号
        wrapper.like(StringUtils.isNotBlank(query.getNumber()),
                Bus::getBusNumber, query.getNumber());

        // 出发地
        wrapper.like(StringUtils.isNotBlank(query.getOrigin()),
                Bus::getOrigin, query.getOrigin());

        // 目的地
        wrapper.like(StringUtils.isNotBlank(query.getDestination()),
                Bus::getDestination, query.getDestination());

        // =========================
        // ✅ 出发日期筛选
        // =========================
        if (query.getDepartureTime() != null) {

            LocalDate date = query.getDepartureTime();

            wrapper.between(
                    Bus::getDepartureTime,
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
                    Bus::getArrivalTime,
                    date.atStartOfDay(),
                    date.atTime(23, 59, 59)
            );
        }

        // 查询航班ID
        return busMapper.selectList(wrapper)
                .stream()
                .map(Bus::getId)
                .collect(Collectors.toList());
    }

    // 缓存机票信息
    @Override
    public void buildCache(List<Order> orders, TransportCache cache) {

        List<Long> ids = orders.stream()
                .filter(o -> TransportType.BUS.equals(o.getTransportType()))
                .map(Order::getTicketId)
                .distinct()
                .toList();

        if (!ids.isEmpty()) {
            Map<Long, Bus> map = busService.listByIds(ids)
                    .stream()
                    .collect(Collectors.toMap(Bus::getId, f -> f));

            cache.setBusMap(map);
        }
    }

    // 机票信息填充返回给用户
    @Override
    public void fillTransportInfo(OrderUserVO vo, Order order, TransportCache cache) {

        Bus bus = cache.getBusMap().get(order.getTicketId());

        if (bus != null) {
            vo.setTicketId(bus.getId());
            vo.setNumber(bus.getBusNumber());
            vo.setName(bus.getBusName());
            vo.setOrigin(bus.getOrigin());
            vo.setDestination(bus.getDestination());
            vo.setDepartureTime(bus.getDepartureTime());
            vo.setArrivalTime(bus.getArrivalTime());
        }
    }

    // 减少座位
    @Override
    public Integer reduceSeat(Long id, Integer count, Integer version) {
        return busMapper.reduceSeat(id, count, version);
    }

    // 增加座位
    @Override
    public Integer addSeat(Long id, Integer count, Integer version) {
        return busMapper.addSeat(id, count, version);
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
