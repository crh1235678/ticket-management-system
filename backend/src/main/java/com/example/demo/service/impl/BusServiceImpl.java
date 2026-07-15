package com.example.demo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.DTO.article.ArticleQueryDTO;
import com.example.demo.DTO.bus.BusDTO;
import com.example.demo.DTO.bus.BusQueryDTO;
import com.example.demo.VO.article.ArticleCatalogVO;
import com.example.demo.VO.article.ArticleVO;
import com.example.demo.VO.bus.BusUserVO;
import com.example.demo.VO.bus.BusVO;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.ResultCode;
import com.example.demo.entity.Article;
import com.example.demo.entity.transport.Bus;
import com.example.demo.mapper.BusMapper;
import com.example.demo.service.BusService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class BusServiceImpl extends ServiceImpl<BusMapper, Bus> implements BusService {

    @Resource
    private BusMapper busMapper;


    //=========================公共业务逻辑方法====================================================================

    // 普通用户返回 ArticleCatalogVO
    private IPage<BusUserVO> listBussForUser(BusQueryDTO query) {
        Page<Bus> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Bus> wrapper = new LambdaQueryWrapper<>();

        // 有条件才加
        wrapper.like(
                StringUtils.isNotBlank(query.getOrigin()),
                Bus::getOrigin,
                query.getOrigin()
        );

        wrapper.like(
                StringUtils.isNotBlank(query.getDestination()),
                Bus::getDestination,
                query.getDestination()
        );
        // 时间条件
        if (query.getDepartureStart() != null && query.getDepartureEnd() != null) {
            wrapper.between(
                    Bus::getDepartureTime,
                    query.getDepartureStart(),
                    query.getDepartureEnd()
            );
        }
        // 只查有票
        wrapper.gt(Bus::getSeatRemaining, 0);
        // 按出发时间排序
        wrapper.orderByAsc(Bus::getDepartureTime);

        Page<Bus> busPage = busMapper.selectPage(page, wrapper);

        // 把查询到的Bus 转为 BusUserVO 并存储到voList里
        List<BusUserVO> voList = busPage.getRecords().stream().map(bus -> {
            BusUserVO vo = new BusUserVO();
            BeanUtils.copyProperties(bus, vo);
            return vo;
        }).toList();

        // 重组分页
        Page<BusUserVO> voPage = new Page<>(
                busPage.getCurrent(),
                busPage.getSize(),
                busPage.getTotal()
        );
        voPage.setRecords(voList);

        return voPage;
    }

    // 管理员返回 BusVO
    private IPage<BusVO> listBussForAdmin(BusQueryDTO query) {
        Page<Bus> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Bus> wrapper = new LambdaQueryWrapper<>();

        //构造查询条件
        // 航班号模糊查询
        wrapper.like(
                StringUtils.isNotBlank(query.getBusNumber()),
                Bus::getBusNumber,
                query.getBusNumber()
        );
        // 出发地
        wrapper.like(
                StringUtils.isNotBlank(query.getOrigin()),
                Bus::getOrigin,
                query.getOrigin()
        );
        // 目的地
        wrapper.like(
                StringUtils.isNotBlank(query.getDestination()),
                Bus::getDestination,
                query.getDestination()
        );

        // 按出发时间排序
        wrapper.orderByAsc(Bus::getDepartureTime);

        Page<Bus> busPage = busMapper.selectPage(page, wrapper);

        List<BusVO> voList = busPage.getRecords().stream().map(bus -> {
            BusVO vo = new BusVO();
            BeanUtils.copyProperties(bus, vo);
            return vo;
        }).toList();

        Page<BusVO> voPage = new Page<>(
                busPage.getCurrent(),
                busPage.getSize(),
                busPage.getTotal()
        );
        voPage.setRecords(voList);

        return voPage;
    }
    //============================实际业务逻辑==================================================================

    @Override
    public IPage<?> listBuss(BusQueryDTO query, Boolean isAdmin) {
        if (isAdmin) {
            return listBussForAdmin(query);  // 返回 IPage<BusVO>
        } else {
            return listBussForUser(query);   // 返回 IPage<BusUserVO>
        }
    }

    @Override
    public void addBus(BusDTO dto) {

        // 1. 校验航班号唯一
        LambdaQueryWrapper<Bus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bus::getBusNumber, dto.getBusNumber());
        if (this.count(wrapper) > 0) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "航班号已存在");
        }

        // 2. DTO → 实体
        Bus bus = new Bus();
        // 属性复制,BeanUtils相当于bus.setOrigin(dto.getOrigin());简化操作
        BeanUtils.copyProperties(dto, bus);

//        3. 系统字段,后续配置了MetaObjectHandler配合@TableField(fill = FieldFill.INSERT)所以不需要自己设置了
//        LocalDateTime now = LocalDateTime.now();
//        bus.setCreateTime(now);
//        bus.setUpdateTime(now);

        // 4. 保存
        this.save(bus);
    }

    @Override
    public void updateBus(BusDTO dto) {
        if (dto.getId() == null) {
            throw new BussinessException(ResultCode.PARAM_ERROR);
        }
        Bus bus = getById(dto.getId());
        if (bus == null) {
            throw new BussinessException(ResultCode.BUS_NOT_EXIST);
        }
        // 忽略 createTime,id,uptime，防止被覆盖
        // 为什么要忽略这些字段，因为BeanUtils.copyProperties()是复制粘贴操作会覆盖掉createTime。
        // 系统时间字段和id字段采用后端自动填充，避免前端篡改数据，提高安全性
        BeanUtils.copyProperties(dto, bus, "id", "createTime", "updateTime");
        this.updateById(bus);
    }
}

