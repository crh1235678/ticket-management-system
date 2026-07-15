package com.example.demo.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.DTO.train.TrainDTO;
import com.example.demo.DTO.train.TrainQueryDTO;
import com.example.demo.DTO.user.QueryDTO;
import com.example.demo.VO.train.TrainVO;
import com.example.demo.VO.order.OrderUserVO;
import com.example.demo.common.result.Result;
import com.example.demo.common.util.SaTokenUtil;
import com.example.demo.entity.transport.Train;
import com.example.demo.service.TrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrainController {
    @Resource
    private TrainService trainService;

    // ========================= 通用接口 ===================================================================================
    @GetMapping("/train/list")
    @SaCheckLogin
    //这里存在安全问题，见文档问题/接口输出问题
    public Result<IPage<?>> list(TrainQueryDTO query) {
        boolean isAdmin = StpUtil.hasRole("admin");
        return Result.data(trainService.listTrains(query, isAdmin));
    }
    // ========================= 用户接口==================================================================================


    // ========================= 管理员接口 ==================================================================================

    // 添加航班
    @PostMapping("/train/add")
    @SaCheckRole("admin")
    public Result<String> add(@Valid @RequestBody TrainDTO dto) {
        trainService.addTrain(dto);
        return Result.success();
    }

    // 删除航班（支持批量）
    @PostMapping("/train/del")
    @SaCheckRole("admin")
    public Result<String> del(@RequestParam List<Long> ids) {
        trainService.removeByIds(ids);
        return Result.success();
    }

    // 修改航班
    @PostMapping("/train/update")
    @SaCheckRole("admin")
    public Result<String> update(@Valid @RequestBody TrainDTO dto) {

        trainService.updateTrain(dto);
        return Result.success();
    }


}
