package com.example.demo.factory;


import com.example.demo.common.enums.TransportType;
import com.example.demo.entity.transport.BaseTransport;
import com.example.demo.entity.transport.Flight;
import com.example.demo.mapper.FlightMapper;
import com.example.demo.service.impl.BusServiceImpl;
import com.example.demo.service.impl.FlightServiceImpl;
import com.example.demo.service.impl.TrainServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransportFactory {

    @Resource
    private FlightServiceImpl flightService;
    @Resource
    private TrainServiceImpl trainService;
    @Resource
    private BusServiceImpl busService;
    @Resource
    private FlightMapper flightMapper;

    public BaseTransport getTransport(TransportType type, Long id) {
        return switch (type) {
            case FLIGHT -> flightService.getById(id);
            case TRAIN  -> trainService.getById(id);
            case BUS    -> busService.getById(id);
            default -> throw new RuntimeException("未知交通类型");
        };
    }



    // 并发问题残党
/*    public void updateTransport(TransportType type, BaseTransport transport) {
        switch (type) {
            case FLIGHT -> flightService.updateById((Flight) transport);
            //case TRAIN  -> trainService.updateById((Train) transport);
            //case BUS    -> busService.updateById((Bus) transport);
        }
    }*/
}
