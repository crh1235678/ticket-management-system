package com.example.demo.service.cache;

import com.example.demo.entity.transport.Bus;
import com.example.demo.entity.transport.Flight;
import com.example.demo.entity.transport.Train;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TransportCache {

    private Map<Long, Flight> flightMap = new HashMap<>();

    private Map<Long, Train> trainMap = new HashMap<>();

    private Map<Long, Bus> busMap = new HashMap<>();
}