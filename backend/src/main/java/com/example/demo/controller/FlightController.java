package com.example.demo.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.DTO.flight.FlightDTO;
import com.example.demo.DTO.flight.FlightQueryDTO;
import com.example.demo.common.result.Result;
import com.example.demo.service.FlightService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FlightController {
    @Resource
    private FlightService flightService;

    // ========================= 通用接口 ===================================================================================
    @GetMapping("/flight/list")
    @SaCheckLogin
    //这里存在安全问题，见文档问题/接口输出问题
    public Result<IPage<?>> list(FlightQueryDTO query) {
        boolean isAdmin = StpUtil.hasRole("admin");
        return Result.data(flightService.listFlights(query, isAdmin));
    }
    // ========================= 用户接口==================================================================================


    // ========================= 管理员接口 ==================================================================================

     // 添加航班
     @PostMapping("/flight/add")
     @SaCheckRole("admin")
     public Result<String> add(@Valid @RequestBody FlightDTO dto) {
         flightService.addFlight(dto);
         return Result.success();
     }

    // 删除航班（支持批量）
    @PostMapping("/flight/del")
    @SaCheckRole("admin")
    public Result<String> del(@RequestParam List<Long> ids) {
        flightService.removeByIds(ids);
        return Result.success();
    }

    // 修改航班
    @PostMapping("/flight/update")
    @SaCheckRole("admin")
    public Result<String> update(@Valid @RequestBody FlightDTO dto) {

        flightService.updateFlight(dto);
        return Result.success();
    }


}
