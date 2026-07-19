package com.example.demo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.DTO.article.ArticleQueryDTO;
import com.example.demo.DTO.train.TrainDTO;
import com.example.demo.DTO.train.TrainQueryDTO;
import com.example.demo.VO.article.ArticleCatalogVO;
import com.example.demo.VO.article.ArticleVO;
import com.example.demo.VO.train.TrainUserVO;
import com.example.demo.VO.train.TrainVO;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.ResultCode;
import com.example.demo.entity.Article;
import com.example.demo.entity.transport.Train;
import com.example.demo.mapper.TrainMapper;
import com.example.demo.common.enums.TransportType;
import com.example.demo.common.util.RedisTransportCache;
import com.example.demo.service.TrainService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements TrainService {

    @Resource
    private TrainMapper trainMapper;

    @Resource
    private RedisTransportCache redisTransportCache;


    //=========================公共业务逻辑方法====================================================================

    // 普通用户返回 ArticleCatalogVO
    private IPage<TrainUserVO> listTrainsForUser(TrainQueryDTO query) {
        Page<Train> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();

        // 有条件才加
        wrapper.like(
                StringUtils.isNotBlank(query.getOrigin()),
                Train::getOrigin,
                query.getOrigin()
        );

        wrapper.like(
                StringUtils.isNotBlank(query.getDestination()),
                Train::getDestination,
                query.getDestination()
        );
        // 时间条件
        if (query.getDepartureStart() != null && query.getDepartureEnd() != null) {
            wrapper.between(
                    Train::getDepartureTime,
                    query.getDepartureStart(),
                    query.getDepartureEnd()
            );
        }
        // 只查有票
        wrapper.gt(Train::getSeatRemaining, 0);
        // 按出发时间排序
        wrapper.orderByAsc(Train::getDepartureTime);

        Page<Train> trainPage = trainMapper.selectPage(page, wrapper);

        // 把查询到的Train 转为 TrainUserVO 并存储到voList里
        List<TrainUserVO> voList = trainPage.getRecords().stream().map(train -> {
            TrainUserVO vo = new TrainUserVO();
            BeanUtils.copyProperties(train, vo);
            return vo;
        }).toList();

        // 重组分页
        Page<TrainUserVO> voPage = new Page<>(
                trainPage.getCurrent(),
                trainPage.getSize(),
                trainPage.getTotal()
        );
        voPage.setRecords(voList);

        return voPage;
    }

    // 管理员返回 TrainVO
    private IPage<TrainVO> listTrainsForAdmin(TrainQueryDTO query) {
        Page<Train> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();

        //构造查询条件
        // 航班号模糊查询
        wrapper.like(
                StringUtils.isNotBlank(query.getTrainNumber()),
                Train::getTrainNumber,
                query.getTrainNumber()
        );
        // 出发地
        wrapper.like(
                StringUtils.isNotBlank(query.getOrigin()),
                Train::getOrigin,
                query.getOrigin()
        );
        // 目的地
        wrapper.like(
                StringUtils.isNotBlank(query.getDestination()),
                Train::getDestination,
                query.getDestination()
        );

        // 按出发时间排序
        wrapper.orderByAsc(Train::getDepartureTime);

        Page<Train> trainPage = trainMapper.selectPage(page, wrapper);

        List<TrainVO> voList = trainPage.getRecords().stream().map(train -> {
            TrainVO vo = new TrainVO();
            BeanUtils.copyProperties(train, vo);
            return vo;
        }).toList();

        Page<TrainVO> voPage = new Page<>(
                trainPage.getCurrent(),
                trainPage.getSize(),
                trainPage.getTotal()
        );
        voPage.setRecords(voList);

        return voPage;
    }
    //============================实际业务逻辑==================================================================

    @Override
    public IPage<?> listTrains(TrainQueryDTO query, Boolean isAdmin) {
        if (isAdmin) {
            return listTrainsForAdmin(query);  // 返回 IPage<TrainVO>
        } else {
            return listTrainsForUser(query);   // 返回 IPage<TrainUserVO>
        }
    }

    @Override
    public void addTrain(TrainDTO dto) {

        // 1. 校验航班号唯一
        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Train::getTrainNumber, dto.getTrainNumber());
        if (this.count(wrapper) > 0) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "航班号已存在");
        }

        // 2. DTO → 实体
        Train train = new Train();
        // 属性复制,BeanUtils相当于train.setOrigin(dto.getOrigin());简化操作
        BeanUtils.copyProperties(dto, train);

//        3. 系统字段,后续配置了MetaObjectHandler配合@TableField(fill = FieldFill.INSERT)所以不需要自己设置了
//        LocalDateTime now = LocalDateTime.now();
//        train.setCreateTime(now);
//        train.setUpdateTime(now);

        // 4. 保存
        this.save(train);
    }

    @Override
    public void updateTrain(TrainDTO dto) {
        if (dto.getId() == null) {
            throw new BussinessException(ResultCode.PARAM_ERROR);
        }
        Train train = getById(dto.getId());
        if (train == null) {
            throw new BussinessException(ResultCode.TRAIN_NOT_EXIST);
        }
        // 忽略 createTime,id,uptime，防止被覆盖
        // 为什么要忽略这些字段，因为BeanUtils.copyProperties()是复制粘贴操作会覆盖掉createTime。
        // 系统时间字段和id字段采用后端自动填充，避免前端篡改数据，提高安全性
        BeanUtils.copyProperties(dto, train, "id", "createTime", "updateTime");
        this.updateById(train);
        // 清除 Redis 缓存，下次查订单时自动重新加载
        redisTransportCache.evict(TransportType.TRAIN, dto.getId());
    }
}

