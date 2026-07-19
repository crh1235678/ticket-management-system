package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.DTO.article.ArticleQueryDTO;
import com.example.demo.DTO.flight.FlightDTO;
import com.example.demo.DTO.flight.FlightQueryDTO;
import com.example.demo.VO.article.ArticleCatalogVO;
import com.example.demo.VO.article.ArticleVO;
import com.example.demo.VO.flight.FlightUserVO;
import com.example.demo.VO.flight.FlightVO;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.ResultCode;
import com.example.demo.entity.Article;
import com.example.demo.entity.transport.Flight;
import com.example.demo.mapper.FlightMapper;
import com.example.demo.common.enums.TransportType;
import com.example.demo.common.util.RedisTransportCache;
import com.example.demo.service.FlightService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class FlightServiceImpl extends ServiceImpl<FlightMapper, Flight> implements FlightService {

    @Resource
    private FlightMapper flightMapper;

    @Resource
    private RedisTransportCache redisTransportCache;


    //=========================公共业务逻辑方法====================================================================

    // 普通用户返回 ArticleCatalogVO
    private IPage<FlightUserVO> listFlightsForUser(FlightQueryDTO query) {
        Page<Flight> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Flight> wrapper = new LambdaQueryWrapper<>();

        // 有条件才加
        wrapper.like(
                StringUtils.isNotBlank(query.getOrigin()),
                Flight::getOrigin,
                query.getOrigin()
        );

        wrapper.like(
                StringUtils.isNotBlank(query.getDestination()),
                Flight::getDestination,
                query.getDestination()
        );
        // 时间条件
        if (query.getDepartureStart() != null && query.getDepartureEnd() != null) {
            wrapper.between(
                    Flight::getDepartureTime,
                    query.getDepartureStart(),
                    query.getDepartureEnd()
            );
        }
        // 只查有票
        wrapper.gt(Flight::getSeatRemaining, 0);
        // 按出发时间排序
        wrapper.orderByAsc(Flight::getDepartureTime);

        Page<Flight> flightPage = flightMapper.selectPage(page, wrapper);

        // 把查询到的Flight 转为 FlightUserVO 并存储到voList里
        List<FlightUserVO> voList = flightPage.getRecords().stream().map(flight -> {
            FlightUserVO vo = new FlightUserVO();
            BeanUtils.copyProperties(flight, vo);
            return vo;
        }).toList();

        // 重组分页
        Page<FlightUserVO> voPage = new Page<>(
                flightPage.getCurrent(),
                flightPage.getSize(),
                flightPage.getTotal()
        );
        voPage.setRecords(voList);

        return voPage;
    }

    // 管理员返回 FlightVO
    private IPage<FlightVO> listFlightsForAdmin(FlightQueryDTO query) {
        Page<Flight> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Flight> wrapper = new LambdaQueryWrapper<>();

        //构造查询条件
        // 航班号模糊查询
        wrapper.like(
                StringUtils.isNotBlank(query.getFlightNumber()),
                Flight::getFlightNumber,
                query.getFlightNumber()
        );
        // 出发地
        wrapper.like(
                StringUtils.isNotBlank(query.getOrigin()),
                Flight::getOrigin,
                query.getOrigin()
        );
        // 目的地
        wrapper.like(
                StringUtils.isNotBlank(query.getDestination()),
                Flight::getDestination,
                query.getDestination()
        );

        // 按出发时间排序
        wrapper.orderByAsc(Flight::getDepartureTime);

        Page<Flight> flightPage = flightMapper.selectPage(page, wrapper);

        List<FlightVO> voList = flightPage.getRecords().stream().map(flight -> {
            FlightVO vo = new FlightVO();
            BeanUtils.copyProperties(flight, vo);
            return vo;
        }).toList();

        Page<FlightVO> voPage = new Page<>(
                flightPage.getCurrent(),
                flightPage.getSize(),
                flightPage.getTotal()
        );
        voPage.setRecords(voList);

        return voPage;
    }
    //============================实际业务逻辑==================================================================

    @Override
    public IPage<?> listFlights(FlightQueryDTO query, Boolean isAdmin) {
        if (isAdmin) {
            return listFlightsForAdmin(query);  // 返回 IPage<FlightVO>
        } else {
            return listFlightsForUser(query);   // 返回 IPage<FlightUserVO>
        }
    }

    @Override
    public void addFlight(FlightDTO dto) {

        // 1. 校验航班号唯一
        LambdaQueryWrapper<Flight> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Flight::getFlightNumber, dto.getFlightNumber());
        if (this.count(wrapper) > 0) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "航班号已存在");
        }

        // 2. DTO → 实体
        Flight flight = new Flight();
        // 属性复制,BeanUtils相当于flight.setOrigin(dto.getOrigin());简化操作
        BeanUtils.copyProperties(dto, flight);

//        3. 系统字段,后续配置了MetaObjectHandler配合@TableField(fill = FieldFill.INSERT)所以不需要自己设置了
//        LocalDateTime now = LocalDateTime.now();
//        flight.setCreateTime(now);
//        flight.setUpdateTime(now);

        // 4. 保存
        this.save(flight);
    }

    @Override
    public void updateFlight(FlightDTO dto) {
        if (dto.getId() == null) {
            throw new BussinessException(ResultCode.PARAM_ERROR);
        }
        Flight flight = getById(dto.getId());
        if (flight == null) {
            throw new BussinessException(ResultCode.FLIGHT_NOT_EXIST);
        }
        // 忽略 createTime,id,uptime，防止被覆盖
        // 为什么要忽略这些字段，因为BeanUtils.copyProperties()是复制粘贴操作会覆盖掉createTime。
        // 系统时间字段和id字段采用后端自动填充，避免前端篡改数据，提高安全性
        BeanUtils.copyProperties(dto, flight, "id", "createTime", "updateTime");
        this.updateById(flight);
        // 清除 Redis 缓存，下次查订单时自动重新加载
        redisTransportCache.evict(TransportType.FLIGHT, dto.getId());
    }
}
