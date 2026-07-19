package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.DTO.order.ChangeTicketDTO;
import com.example.demo.DTO.order.CreateOrderDTO;
import com.example.demo.DTO.order.ModifyOrderDTO;
import com.example.demo.DTO.user.QueryDTO;
import com.example.demo.DTO.order.ChangeNewTicketDTO;
import com.example.demo.VO.article.ArticleCatalogVO;
import com.example.demo.VO.order.OptionalTicketVO;
import com.example.demo.VO.order.OrderInformationVO;
import com.example.demo.VO.order.OrderUserVO;
import com.example.demo.common.enums.OrderStatus;
import com.example.demo.common.enums.TransportType;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.ResultCode;
import com.example.demo.common.util.SaTokenUtil;
import com.example.demo.entity.Order;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.entity.transport.BaseTransport;
import com.example.demo.factory.TransportFactory;
import com.example.demo.mapper.FlightMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import com.example.demo.service.cache.TransportCache;
import com.example.demo.common.util.RedisTransportCache;
import com.example.demo.service.inner.OrderInnerService;
import com.example.demo.strategy.transport.TransportStrategy;
import com.example.demo.strategy.transport.TransportStrategyManager;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Resource
    private TransportFactory transportFactory;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderInnerService orderInnerService;
    @Resource
    private UserService userService;

    @Resource
    private TransportStrategyManager strategyManager;

    @Resource
    private RedisTransportCache redisTransportCache;


    //=========================公共业务逻辑方法====================================================================
    //============================实际业务逻辑==================================================================
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderInformationVO createOrder(CreateOrderDTO dto) {

        Long userId = SaTokenUtil.getLoginUserId();

        // 1️⃣ 使用工厂动态获取票对象
        // System.out.println("crh"+dto.getTicketType()+dto.getTicketId());
        BaseTransport transport = transportFactory.getTransport(dto.getTransportType(), dto.getTransportId());
        if (transport == null) {
            throw new BussinessException(ResultCode.PARAM_ERROR, dto.getTransportType() + "绁ㄤ笉瀛樺湪");
        }

        // 调用内部事务创建并保存订单，主要目的是为了让事务一致性，回滚事务做到统一
        Order order = orderInnerService.createOrderInner(transport, dto, userId);

        // 5️⃣ 返回订单信息
        OrderInformationVO information = new OrderInformationVO();
        information.setOrderId(order.getId());
        information.setTicketId(order.getTicketId());
        information.setTransportType(dto.getTransportType());

        return information;

    }

    // 前台取消订单的基本业务操作（注意这个是没付钱的情况）
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void cancelOrder(Long orderId) {
        // 1️⃣ 获取当前登录用户，查看该用户是否拥有该订单，防止用户恶意传入他人的订单进行修改
        Long userId = SaTokenUtil.getLoginUserId();
        // 2️⃣ 查订单，确保订单存在且属于当前用户
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BussinessException("Order does not exist");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BussinessException("No permission to cancel others order");
        }
        // 3️⃣ 检查订单状态，只能取消未支付的订单和已经支付的订单
        if (order.getStatus() == OrderStatus.CANCELLED ) {
            throw new BussinessException("订单已取消，无法操作");
        }
        // 4️⃣ 获取票对象
        BaseTransport transport = transportFactory.getTransport(order.getTransportType(), order.getTicketId());
        if (transport == null) {
            throw new BussinessException("对应票不存在");
        }

        orderInnerService.cancelOrderInner(order, transport);
    }

    // 前台退票的基本业务操作
    @Override
    @Transactional
    public void refundTicket(Long orderId){
        // 1锔忊儯 鑾峰彇褰撳墠鐧诲綍鐢ㄦ埛,鏌ョ湅璇ョ敤鎴锋槸鍚︽嫢鏈夎璁㈠崟锛岄槻姝㈢敤鎴锋伓鎰忎紶鍏ヤ粬浜虹殑璁㈠崟杩涜淇敼
        Long userId = SaTokenUtil.getLoginUserId();
        User user = userService.getById(userId);
        // 2锔忊儯 鏌ヨ鍗曪紝纭繚璁㈠崟瀛樺湪涓斿睘浜庡綋鍓嶇敤鎴?
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BussinessException("Order does not exist");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BussinessException("No permission to cancel others order");
        }
        // 3锔忊儯 妫€鏌ヨ鍗曠姸鎬侊紝鍙兘鍙栨秷鏈敮浠樼殑璁㈠崟鍜屽凡缁忔敮浠樼殑璁㈠崟
        if (order.getStatus() == OrderStatus.CANCELLED ) {
            throw new BussinessException("璁㈠崟宸插彇娑堬紝鏃犳硶鎿嶄綔");
        }
        // 4锔忊儯 鑾峰彇绁ㄥ璞?
        BaseTransport transport = transportFactory.getTransport(order.getTransportType(), order.getTicketId());
        if (transport == null) {
            throw new BussinessException("瀵瑰簲绁ㄤ笉瀛樺湪");
        }
        orderInnerService.refundTicketInner(order, transport, user);
    }
    @Override
    @Transactional
    public void payOrder(Long orderId) {
        Long userId = SaTokenUtil.getLoginUserId();
        User user = userService.getById(userId);
        Order order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BussinessException(ResultCode.NO_PERMISSION, "Order not exist or no permission");
        }
        if (order.getStatus() != OrderStatus.UNPAID) {
            throw new BussinessException(ResultCode.ORDER_STATUS, "Order status not payable");
        }

        // 创建充值流水数据体，把数据写入数据库
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(order.getTotalPrice());
        transaction.setType("PURCHASE");
        transaction.setPayType("BALANCE");

        orderInnerService.payOrderInner(order, user, transaction);
    }

    @Override
    public IPage<OptionalTicketVO> changeTicketList(ChangeTicketDTO dto) {

        // 判定订单状态
        Order order = this.getById(dto.getId());
        if (order == null) {
            throw new BussinessException(ResultCode.ORDER_NOT_EXIST);
        }
        if (!order.getUserId().equals(SaTokenUtil.getLoginUserId())) {
            throw new BussinessException(ResultCode.NO_PERMISSION);
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BussinessException(ResultCode.ORDER_STATUS);
        }
        TransportStrategy strategy = strategyManager.get(dto.getTransportType());
        if (strategy != null) {
            return strategy.changeTickets(dto, order);
        }else{
            throw new BussinessException(ResultCode.ERROR,"Change not supported");
        }
    }
    @Override
    @Transactional
    public void changeConfirm(ChangeNewTicketDTO dto) {

        Long userId = SaTokenUtil.getLoginUserId();
        User user = userService.getById(userId);

        Order oldOrder = orderMapper.selectById(dto.getOldId());
        if (oldOrder == null || !oldOrder.getUserId().equals(userId)) {
            throw new BussinessException(ResultCode.NO_PERMISSION);
        }

        if (oldOrder.getStatus() != OrderStatus.PAID) {
            throw new BussinessException("Only paid orders can be changed");
        }

        BaseTransport transport = transportFactory.getTransport(dto.getTransportType(), dto.getTransportId());

        BigDecimal newPrice = transport.getPrice()
                .multiply(BigDecimal.valueOf(dto.getSeatCount()));

        BigDecimal diffPrice = newPrice.subtract(oldOrder.getTotalPrice());

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(diffPrice.abs());
        transaction.setType("CHANGE");
        transaction.setPayType("BALANCE");

        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setTransportId(dto.getTransportId());
        createOrderDTO.setTransportType(dto.getTransportType());
        createOrderDTO.setSeatCount(dto.getSeatCount());

        orderInnerService.changeTicketInner(
                oldOrder,
                transport,
                dto,
                createOrderDTO,
                user,
                diffPrice,
                transaction
        );
    }

    @Override
    public IPage<OrderUserVO> queryPage(QueryDTO query, Long userId, boolean isAdmin) {

        if (isAdmin) {
            return getAllOrders(query);
        } else {
            //System.out.println("departureTime = " + query.getDepartureTime());
            return getUserOrders(query, userId);
        }
    }

    private IPage<OrderUserVO> getUserOrders(QueryDTO query, Long userId) {

        Page<Order> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        // =========================
        // ✅ 跨表筛选：先查 transport
        // =========================
        if (StringUtils.isNotBlank(query.getOrigin())
                || StringUtils.isNotBlank(query.getDestination())
                || StringUtils.isNotBlank(query.getNumber())
                || query.getDepartureTime() != null
                || query.getArrivalTime() != null) {

            List<Long> transportIds = new ArrayList<>();

            for (TransportStrategy strategy : strategyManager.getAll()) {

                // 鉁?鍙墽琛屽尮閰嶇殑浜ら€氱被鍨嬶紙鏍稿績浼樺寲馃敟锛?
                if (StringUtils.isNotBlank(query.getTransportType())
                        && !strategy.getType().name().equals(query.getTransportType())) {
                    continue;
                }

                transportIds.addAll(strategy.queryIds(query));
            }

            // 鉂楀叧閿細娌℃湁缁撴灉鐩存帴杩斿洖绌?
            if (transportIds.isEmpty()) {
                return new Page<>(query.getPageNum(), query.getPageSize());
            }

            wrapper.in(Order::getTicketId, transportIds);
        }

        // =========================
        // 鉁?Order 琛ㄧ瓫閫?
        // =========================

        // 当前用户
        wrapper.eq(Order::getUserId, userId);

        // 闄愬埗鐢ㄦ埛鍙兘鐪嬭繖鍑犵鐘舵€侊紝杩欐牱鍋氶槻姝㈢敤鎴蜂慨鏀箂tatus鏌ョ湅鍏朵粬淇℃伅
        List<OrderStatus> allowStatus = Arrays.asList(
                OrderStatus.UNPAID,
                OrderStatus.PAID
        );

        if (StringUtils.isNotBlank(query.getStatus())) {
            OrderStatus status = OrderStatus.valueOf(query.getStatus());

            if (!allowStatus.contains(status)) {
                throw new BussinessException(ResultCode.NO_PERMISSION);
            }

            wrapper.eq(Order::getStatus, status);
        }

        // 交通类型
        wrapper.eq(
                StringUtils.isNotBlank(query.getTransportType()),
                Order::getTransportType,
                query.getTransportType()
        );

        // （可选优化）排序
        wrapper.orderByDesc(Order::getCreateTime);

        return baseQuery(page, wrapper);
    }

    private IPage<OrderUserVO> getAllOrders(QueryDTO query) {

        Page<Order> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        // 可选筛选
        // 状态不能模糊，因为就三个状态：未支付，已支付，已取消
        wrapper.eq(
                StringUtils.isNotBlank(query.getStatus()),
                Order::getStatus, //鍛婅瘔 MyBatis-Plus 鈥滄垜瑕佹搷浣滅殑鏄?Admin 瀹炰綋閲岀殑 name 杩欎釜瀛楁鈥濓紝鍛婅瘔妗嗘灦鈥滄槸鍝釜瀛楁
                query.getStatus() //鍛婅瘔 MyBatis-Plus 鈥滄垜瑕佹搷浣滅殑鏄?Admin 瀹炰綋閲岀殑 name 杩欎釜瀛楁鈥濓紝鍛婅瘔妗嗘灦鈥滄槸鍝釜瀛楁
        );
        wrapper.like(
                StringUtils.isNotBlank(query.getTransportType()),
                Order::getTransportType, //鍛婅瘔 MyBatis-Plus 鈥滄垜瑕佹搷浣滅殑鏄?Admin 瀹炰綋閲岀殑 name 杩欎釜瀛楁鈥濓紝鍛婅瘔妗嗘灦鈥滄槸鍝釜瀛楁
                query.getTransportType() //鍛婅瘔 MyBatis-Plus 鈥滄垜瑕佹搷浣滅殑鏄?Admin 瀹炰綋閲岀殑 name 杩欎釜瀛楁鈥濓紝鍛婅瘔妗嗘灦鈥滄槸鍝釜瀛楁
        );
        wrapper.like(
                query.getUserId() != null,
                Order::getUserId,
                query.getUserId()
        );
        return baseQuery(page, wrapper);
    }

    // 这个优化"解决两个问题：数据库多次查询 + 循环遍历多"
    // 前者解决就是将一次查询所有都保存下来，而不是在循环里去数据库查找数据，后者就是用hash表解决遍历多的问题
    private IPage<OrderUserVO> baseQuery(Page<Order> page, LambdaQueryWrapper<Order> wrapper) {

        // 1️⃣ 查询订单
        IPage<Order> orderPage = orderMapper.selectPage(page, wrapper);
        List<Order> orders = orderPage.getRecords();
        if (orders.isEmpty()) {
            return new Page<>(page.getCurrent(), page.getSize());
        }

        // =========================
        // 2️⃣ 批量查询用户（一次SQL）
        // =========================
/*         Stream 鎶婇泦鍚堣浆鎹㈡垚涓€鏉♀€滄暟鎹祦鈥濓紝
         鐒跺悗瀵规祦涓殑鍏冪礌閫愪釜杩涜娴佹按绾垮紡澶勭悊,
         鐢╩ap鍑芥暟鎶妎rder绫昏浆鎹㈡垚userid鍘婚噸锛?
         鐒跺悗鐢╟ollect灏嗘暟鎹祦鍙樻垚鏂扮殑鍒楄〃*/
        List<Long> userIds = orders.stream()
                .map(Order::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> userMap = new HashMap<>();
        // 这里就是解决N+1问题，批量查询用户并缓存。一次SQL查询所有用户，并转成Hash表相较于list表减少循环查询次数
        if (!userIds.isEmpty()) {
            userMap = userService.listByIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }


        // =========================
        // 3️⃣ 批量查询交通（一次SQL）
        // 构建票务缓存
        // =========================
        TransportCache cache = new TransportCache();

        for (TransportStrategy strategy : strategyManager.getAll()) {
            strategy.buildCache(orderPage.getRecords(), cache);
        }

        // =========================
        // 🌟 将车次信息写入 Redis 缓存（供后续请求复用）
        // =========================
        if (!cache.getTrainMap().isEmpty()) {
            redisTransportCache.batchPut(new ArrayList<>(cache.getTrainMap().values()));
        }
        if (!cache.getFlightMap().isEmpty()) {
            redisTransportCache.batchPut(new ArrayList<>(cache.getFlightMap().values()));
        }
        if (!cache.getBusMap().isEmpty()) {
            redisTransportCache.batchPut(new ArrayList<>(cache.getBusMap().values()));
        }

        // =========================
        // 4️⃣ VO组装（无SQL🔥）
        // =========================
        List<OrderUserVO> voList = new ArrayList<>();

        for (Order order : orders) {

            OrderUserVO vo = new OrderUserVO();

            vo.setTransportType(order.getTransportType());
            vo.setSeatCount(order.getSeatCount());
            vo.setTotalPrice(order.getTotalPrice());
            vo.setStatus(order.getStatus());
            vo.setId(order.getId());
            vo.setUserId(order.getUserId());

            // 鉁?浣跨敤缂撳瓨锛堝叧閿偣馃敟锛?
            User user = userMap.get(order.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }

            vo.setCreateTime(order.getCreateTime());
            vo.setUpdateTime(order.getUpdateTime());

            // 不再查数据库，查缓存信息
            // 根据交通类型采用单策略运行，不在遍历策略运行
            TransportStrategy strategy = strategyManager.get(vo.getTransportType());
            if (strategy != null) {
                strategy.fillTransportInfo(vo, order, cache);
            }
            // 这是遍历策略运行，会降低运行性能
//          for (TransportStrategy strategy : strategyManager.getAll()) {
//              strategy.fillTransportInfo(vo, order, cache);
//          }

            voList.add(vo);
        }

        // =========================
        // 5️⃣ 返回分页
        // =========================
        Page<OrderUserVO> result = new Page<>();
        result.setRecords(voList);
        result.setTotal(orderPage.getTotal());
        result.setCurrent(orderPage.getCurrent());
        result.setSize(orderPage.getSize());

        return result;
    }
      // 这个是严重的N+1次查询，严重影响性能，详情见文档N+1次查询
//    private IPage<OrderUserVO> baseQuery(Page<Order> page, LambdaQueryWrapper<Order> wrapper) {
//
//        // 1锔忊儯 鏌ユ暟鎹簱
//        IPage<Order> orderPage = orderMapper.selectPage(page, wrapper);
//
//        List<OrderUserVO> voList = new ArrayList<>();//N+1娆℃煡璇㈤棶棰樼殑1
//
//        // 2锔忊儯 VO杞崲
//        for (Order order : orderPage.getRecords()) { //N+1娆℃煡璇㈤棶棰樼殑N
//
//            OrderUserVO vo = new OrderUserVO();
//
//            vo.setTicketType(TicketType.valueOf(order.getTicketType()));
//            vo.setSeatCount(order.getSeatCount());
//            vo.setTotalPrice(order.getTotalPrice());
//            vo.setStatus(order.getStatus());
//            vo.setOrderId(order.getId());
//            vo.setUserId(order.getUserId());
//            vo.setUsername(userService.getById(order.getUserId()).getUsername()); // 这里又是一个N
//            vo.setCreateTime(order.getCreateTime());
//            vo.setUpdateTime(order.getUpdateTime());
//
//
//            fillTicketInfo(vo, order);
//
//            voList.add(vo);
//        }
//
//        // 3锔忊儯 閲嶆柊灏佽鍒嗛〉
//        Page<OrderUserVO> result = new Page<>();
//        result.setRecords(voList);
//        result.setTotal(orderPage.getTotal());
//        result.setCurrent(orderPage.getCurrent());
//        result.setSize(orderPage.getSize());
//
//        return result;
//    }

    @Override
    public void modifyOrder(ModifyOrderDTO dto) {

        SaTokenUtil.getLoginAdminId();
        Order order = orderMapper.selectById(dto.getId());

        if (dto.getId() == null) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "订单ID不能为空");
        }
        int rows = orderMapper.updateOrderSelective(
                dto.getId(),
                dto.getStatus(),
                dto.getSeatCount(),
                order.getVersion()
        );
        if (rows == 0) {
            throw new BussinessException("Update failed, data may have been modified");
        }
    }
}

