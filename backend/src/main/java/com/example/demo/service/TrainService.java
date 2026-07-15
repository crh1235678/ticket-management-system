package com.example.demo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.DTO.flight.FlightDTO;
import com.example.demo.DTO.flight.FlightQueryDTO;
import com.example.demo.DTO.train.TrainDTO;
import com.example.demo.DTO.train.TrainQueryDTO;
import com.example.demo.entity.transport.Train;

public interface TrainService extends IService<Train> {

    IPage<?> listTrains(TrainQueryDTO query, Boolean isAdmin);

    void addTrain(TrainDTO dto);

    void updateTrain(TrainDTO dto);
}
