package com.example.demo.common.util;

import com.example.demo.common.enums.TransportType;
import com.example.demo.entity.transport.BaseTransport;
import com.example.demo.entity.transport.Bus;
import com.example.demo.entity.transport.Flight;
import com.example.demo.entity.transport.Train;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 版车次缓存
 * <p>
 * 用途：将热门车次信息缓存到 Redis Hash 中，减少数据库查询
 * <p>
 * Redis Key 格式：
 * - ticket:cache:train:{id}  → Hash（Train 字段）
 * - ticket:cache:flight:{id} → Hash（Flight 字段）
 * - ticket:cache:bus:{id}    → Hash（Bus 字段）
 * <p>
 * 过期时间：12 小时（43200 秒）
 * <p>
 * 为啥用 Hash 不用 String？
 * - 车次信息有多个字段（number/name/origin/destination/price 等）
 * - Hash 可以单独读写某个字段，String 得整个序列化/反序列化
 * - Hash 更省内存（小字段场景）
 */
@Component
public class RedisTransportCache {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /** 缓存过期时间：12 小时 */
    private static final long CACHE_TTL_SECONDS = 43200;

    /** Key 前缀 */
    private static final String KEY_PREFIX = "ticket:cache:";

    // ==================== 日期格式化（用于 LocalDateTime 的序列化） ====================
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ==================== 通用方法 ====================

    /**
     * 构建 Redis Key
     * 示例：ticket:cache:train:101
     */
    private String buildKey(TransportType type, Long ticketId) {
        return KEY_PREFIX + type.name().toLowerCase() + ":" + ticketId;
    }

    /**
     * 从 Redis Hash 中读取车次信息，反序列化为 BaseTransport
     *
     * @param type     交通类型
     * @param ticketId 车次 ID
     * @return BaseTransport 对象，如果缓存中没有则返回 null
     */
    public BaseTransport get(TransportType type, Long ticketId) {
        String key = buildKey(type, ticketId);
        HashOperations<String, String, String> ops = stringRedisTemplate.opsForHash();

        // 获取 Hash 的所有字段
        Map<String, String> entries = ops.entries(key);
        if (entries.isEmpty()) {
            return null; // 缓存未命中
        }

        return deserialize(type, entries);
    }

    /**
     * 批量从 Redis Hash 中读取车次信息
     *
     * @param type      交通类型
     * @param ticketIds 车次 ID 列表
     * @return Map<Long, BaseTransport>，key 为车次 ID，value 为车次实体
     */
    public Map<Long, BaseTransport> batchGet(TransportType type, Collection<Long> ticketIds) {
        Map<Long, BaseTransport> result = new HashMap<>();
        if (ticketIds == null || ticketIds.isEmpty()) {
            return result;
        }

        HashOperations<String, String, String> ops = stringRedisTemplate.opsForHash();

        for (Long id : ticketIds) {
            String key = buildKey(type, id);
            Map<String, String> entries = ops.entries(key);
            if (!entries.isEmpty()) {
                result.put(id, deserialize(type, entries));
            }
        }

        return result;
    }

    /**
     * 将车次信息存入 Redis Hash
     *
     * @param transport 车次实体
     * @param <T>       继承 BaseTransport 的类型
     */
    public <T extends BaseTransport> void put(T transport) {
        if (transport == null || transport.getId() == null) {
            return;
        }

        TransportType type = getTransportType(transport);
        String key = buildKey(type, transport.getId());
        Map<String, String> hash = serialize(transport);

        // 存入 Redis Hash，并设置过期时间
        stringRedisTemplate.opsForHash().putAll(key, hash);
        stringRedisTemplate.expire(key, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 批量将车次信息存入 Redis Hash
     *
     * @param transports 车次实体列表
     * @param <T>        继承 BaseTransport 的类型
     */
    public <T extends BaseTransport> void batchPut(List<T> transports) {
        if (transports == null || transports.isEmpty()) {
            return;
        }

        for (T transport : transports) {
            put(transport);
        }
    }

    /**
     * 从 Redis 中删除指定的车次缓存
     * 管理员修改/删除车次后调用此方法
     *
     * @param type     交通类型
     * @param ticketId 车次 ID
     */
    public void evict(TransportType type, Long ticketId) {
        String key = buildKey(type, ticketId);
        stringRedisTemplate.delete(key);
    }

    /**
     * 批量删除车次缓存
     *
     * @param type      交通类型
     * @param ticketIds 车次 ID 列表
     */
    public void batchEvict(TransportType type, Collection<Long> ticketIds) {
        if (ticketIds == null || ticketIds.isEmpty()) {
            return;
        }
        List<String> keys = ticketIds.stream()
                .map(id -> buildKey(type, id))
                .collect(Collectors.toList());
        stringRedisTemplate.delete(keys);
    }

    // ==================== 序列化 / 反序列化 ====================

    /**
     * 将 BaseTransport 转为 Map<String, String>（适配 Redis Hash）
     */
    private Map<String, String> serialize(BaseTransport transport) {
        Map<String, String> hash = new LinkedHashMap<>();
        hash.put("id", String.valueOf(transport.getId()));
        hash.put("origin", nullToEmpty(transport.getOrigin()));
        hash.put("destination", nullToEmpty(transport.getDestination()));
        hash.put("departureTime", transport.getDepartureTime() != null ? transport.getDepartureTime().format(DTF) : "");
        hash.put("arrivalTime", transport.getArrivalTime() != null ? transport.getArrivalTime().format(DTF) : "");
        hash.put("price", transport.getPrice() != null ? transport.getPrice().toString() : "0");
        hash.put("seatTotal", String.valueOf(transport.getSeatTotal()));
        hash.put("seatRemaining", String.valueOf(transport.getSeatRemaining()));

        // 子类特有字段
        if (transport instanceof Train) {
            Train t = (Train) transport;
            hash.put("trainNumber", nullToEmpty(t.getTrainNumber()));
            hash.put("trainName", nullToEmpty(t.getTrainName()));
        } else if (transport instanceof Flight) {
            Flight f = (Flight) transport;
            hash.put("flightNumber", nullToEmpty(f.getFlightNumber()));
            hash.put("flightName", nullToEmpty(f.getFlightName()));
        } else if (transport instanceof Bus) {
            Bus b = (Bus) transport;
            hash.put("busNumber", nullToEmpty(b.getBusNumber()));
            hash.put("busName", nullToEmpty(b.getBusName()));
        }

        return hash;
    }

    /**
     * 将 Redis Hash 的 Map<String, String> 转回 BaseTransport
     */
    private BaseTransport deserialize(TransportType type, Map<String, String> entries) {
        // 根据交通类型创建对应的实体
        BaseTransport transport;
        switch (type) {
            case TRAIN:
                Train train = new Train();
                train.setTrainNumber(entries.getOrDefault("trainNumber", ""));
                train.setTrainName(entries.getOrDefault("trainName", ""));
                transport = train;
                break;
            case FLIGHT:
                Flight flight = new Flight();
                flight.setFlightNumber(entries.getOrDefault("flightNumber", ""));
                flight.setFlightName(entries.getOrDefault("flightName", ""));
                transport = flight;
                break;
            case BUS:
                Bus bus = new Bus();
                bus.setBusNumber(entries.getOrDefault("busNumber", ""));
                bus.setBusName(entries.getOrDefault("busName", ""));
                transport = bus;
                break;
            default:
                return null;
        }

        // 公共字段
        transport.setId(Long.parseLong(entries.getOrDefault("id", "0")));
        transport.setOrigin(entries.getOrDefault("origin", ""));
        transport.setDestination(entries.getOrDefault("destination", ""));
        transport.setDepartureTime(parseDateTime(entries.get("departureTime")));
        transport.setArrivalTime(parseDateTime(entries.get("arrivalTime")));
        transport.setPrice(new BigDecimal(entries.getOrDefault("price", "0")));
        transport.setSeatTotal(Integer.parseInt(entries.getOrDefault("seatTotal", "0")));
        transport.setSeatRemaining(Integer.parseInt(entries.getOrDefault("seatRemaining", "0")));

        return transport;
    }

    // ==================== 工具方法 ====================

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private LocalDateTime parseDateTime(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(str, DTF);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据实体类型获取 TransportType 枚举
     */
    private TransportType getTransportType(BaseTransport transport) {
        if (transport instanceof Train) return TransportType.TRAIN;
        if (transport instanceof Flight) return TransportType.FLIGHT;
        if (transport instanceof Bus) return TransportType.BUS;
        throw new IllegalArgumentException("未知的交通类型: " + transport.getClass().getSimpleName());
    }
}
