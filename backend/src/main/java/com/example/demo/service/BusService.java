package com.example.demo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.DTO.flight.FlightDTO;
import com.example.demo.DTO.flight.FlightQueryDTO;
import com.example.demo.DTO.bus.BusDTO;
import com.example.demo.DTO.bus.BusQueryDTO;
import com.example.demo.entity.transport.Bus;

public interface BusService extends IService<Bus> {

    IPage<?> listBuss(BusQueryDTO query, Boolean isAdmin);

    void addBus(BusDTO dto);

    void updateBus(BusDTO dto);
}
