package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.DTO.flight.FlightDTO;
import com.example.demo.DTO.flight.FlightQueryDTO;
import com.example.demo.VO.flight.FlightVO;
import com.example.demo.entity.transport.Flight;

public interface FlightService extends IService<Flight> {

    IPage<?> listFlights(FlightQueryDTO query, Boolean isAdmin);

    void addFlight(FlightDTO dto);

    void updateFlight(FlightDTO dto);

}
