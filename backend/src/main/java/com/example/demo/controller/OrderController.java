package com.example.demo.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.DTO.order.ChangeTicketDTO;
import com.example.demo.DTO.order.CreateOrderDTO;
import com.example.demo.DTO.order.ModifyOrderDTO;
import com.example.demo.DTO.user.QueryDTO;
import com.example.demo.DTO.order.ChangeNewTicketDTO;
import com.example.demo.VO.order.OptionalTicketVO;
import com.example.demo.VO.order.OrderInformationVO;
import com.example.demo.VO.order.OrderUserVO;
import com.example.demo.common.result.Result;
import com.example.demo.common.util.SaTokenUtil;
import com.example.demo.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class OrderController {
    @Resource
    private OrderService orderService;
    // ========================= 通用接口 ===================================================================================
    // GET 请求默认不支持 @RequestBody
    @GetMapping("/order/list")
    @SaCheckLogin
    //这里存在安全问题，见文档问题/接口输出问题
    public Result<IPage<OrderUserVO>> list(QueryDTO query) {

        // 判断当前用户是否是管理员
        boolean isAdmin = StpUtil.hasRole("admin");
        Long userId = null;
        if (!isAdmin) {
            userId = SaTokenUtil.getLoginUserId();
        }
        return Result.data(orderService.queryPage(query, userId, isAdmin));
    }

    // ========================= 用户接口 ===================================================================================
    @PostMapping("/order/create")
    @SaCheckRole("user")
    // @Valid是自动校验dto上的注解（如 @NotNull、@Min、@NotBlank 等）
    public Result<OrderInformationVO> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
        OrderInformationVO order = orderService.createOrder(dto);
        return Result.data(order); // 返回订单信息给前端展示
    }
    // 取消订单
    @PostMapping("/order/cancel")
    @SaCheckRole("user")
    public Result<String> cancelOrder(@RequestParam Long orderId) {
        orderService.cancelOrder(orderId);
        return Result.success("取消成功");
    }
    // 退票
    // 把他和取消订单分开，因为业务逻辑上存在微小差异，退票还需要回滚用户余额，而取消订单不需要
    @PostMapping("/order/refund")
    @SaCheckRole("user")
    public Result<String> refundTicket(@RequestParam Long orderId) {
        orderService.refundTicket(orderId);
        return Result.success("退票成功");
    }
    // 罗列可供改签的票
    @GetMapping("/order/change/list")
    @SaCheckRole("user")
    public Result<IPage<OptionalTicketVO>> ticketList(ChangeTicketDTO dto) {
        return Result.data(orderService.changeTicketList(dto));
    }
    // 确定改签
    @PostMapping("/order/change/confirm")
    @SaCheckRole("user")
    public Result<String> changeConfirm(@Valid @RequestBody ChangeNewTicketDTO dto) {
        orderService.changeConfirm(dto);
        return Result.success("改签成功");
    }
    // 支付订单
    @PostMapping("/order/pay")
    @SaCheckRole("user")
    public Result<String> payOrder(@RequestParam Long orderId) {
        orderService.payOrder(orderId);
        return Result.success("支付成功");
    }


   // ========================= 管理员接口 ==================================================================================


    @PostMapping("/order/modify")
    @SaCheckRole("admin")
    public Result<String> modifyOrder(@Valid @RequestBody ModifyOrderDTO dto) {
        orderService.modifyOrder(dto);
        return Result.success("修改成功");
    }

    @PostMapping("/order/del")
    @SaCheckRole("admin")
    public Result<String> removeOrder(@RequestParam List<Long> ids) {
        orderService.removeByIds(ids);
        return Result.success("删除成功");
    }
}
