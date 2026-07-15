package com.example.demo.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.DTO.bus.BusDTO;
import com.example.demo.DTO.bus.BusQueryDTO;
import com.example.demo.DTO.user.QueryDTO;
import com.example.demo.VO.bus.BusVO;
import com.example.demo.VO.order.OrderUserVO;
import com.example.demo.common.result.Result;
import com.example.demo.common.util.SaTokenUtil;
import com.example.demo.entity.transport.Bus;
import com.example.demo.service.BusService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BusController {
    @Resource
    private BusService busService;

    // ========================= 通用接口 ===================================================================================
    @GetMapping("/bus/list")
    @SaCheckLogin
    //这里存在安全问题，见文档问题/接口输出问题
    public Result<IPage<?>> list(BusQueryDTO query) {
        boolean isAdmin = StpUtil.hasRole("admin");
        return Result.data(busService.listBuss(query, isAdmin));
    }
    // ========================= 用户接口==================================================================================


    // ========================= 管理员接口 ==================================================================================

    // 添加航班
    @PostMapping("/bus/add")
    //@SaCheckRole("admin")
    public Result<String> add(@Valid @RequestBody BusDTO dto) {
        busService.addBus(dto);
        return Result.success();
    }

    // 删除航班（支持批量）
    @PostMapping("/bus/del")
    @SaCheckRole("admin")
    public Result<String> del(@RequestParam List<Long> ids) {
        busService.removeByIds(ids);
        return Result.success();
    }

    // 修改航班
    @PostMapping("/bus/update")
    @SaCheckRole("admin")
    public Result<String> update(@Valid @RequestBody BusDTO dto) {

        busService.updateBus(dto);
        return Result.success();
    }


}
