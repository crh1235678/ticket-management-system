package com.example.demo.strategy.transport;

import com.example.demo.common.enums.TransportType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// 整体流程：
// 1️⃣ 扫描到 FlightStrategy / TrainStrategy
// 2️⃣ 创建对象
// 3️⃣ 收集成 List<TransportStrategy>
// 4️⃣ 调用 setStrategies()
// 5️⃣ 构建 strategyMap
@Component
public class TransportStrategyManager {

    // 建立不同交通类型和相对应的交通策略的映射
    private final Map<TransportType, TransportStrategy> strategyMap = new HashMap<>();

    @Resource
    // 注入所有的交通策略,自动扫描所有实现了 TransportStrategy 接口 + 有 @Component 的类，把他们放到 strategies列表中
    public void setStrategies(List<TransportStrategy> strategies) {
        for (TransportStrategy strategy : strategies) {
            // 映射添加数据
            strategyMap.put(strategy.getType(), strategy);
        }
    }

    // 返回所有交通策略
    public Collection<TransportStrategy> getAll() {
        return strategyMap.values();
    }

    // 根据交通类型返回指定交通策略
    public TransportStrategy get(TransportType type) {
        return strategyMap.get(type);
    }
}
