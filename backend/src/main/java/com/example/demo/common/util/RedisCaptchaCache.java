package com.example.demo.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 版验证码缓存
 * 取代旧的 ConcurrentHashMap 本地缓存
 *
 * 核心变化：
 * 1. 验证码存入 Redis（独立存储，项目重启不丢）
 * 2. 5 分钟自动过期（无需手动删除）
 * 3. 校验后立即删除（一次性使用，防重复利用）
 *
 * key 命名规范：ticket:captcha:{captchaId}
 * 作用：加上 ticket: 前缀，避免和其他系统的 Redis key 冲突
 */
@Component
public class RedisCaptchaCache {

    // ========================
    // StringRedisTemplate：Spring Boot 自动注入的 Redis 操作工具
    // 专门操作 key 和 value 都是 String 类型的数据
    // 为什么用 StringRedisTemplate 而不是 RedisTemplate？
    // → 验证码 ID 是字符串，验证码文本也是字符串，StringRedisTemplate 最适合
    // → RedisTemplate<Object,Object> 需要额外配置序列化，更复杂
    // ========================
    @Autowired
    private StringRedisTemplate redisTemplate;

    // Redis key 的前缀
    // 防止和其他系统的 key 冲突（你以后有多个系统共用同一个 Redis）
    private static final String CAPTCHA_PREFIX = "ticket:captcha:";

    // 验证码过期时间：5 分钟
    // 足够用户完成注册/登录操作
    // 太短 → 用户还没输完就过期
    // 太长 → 安全隐患
    private static final long CAPTCHA_EXPIRE = 5;

    /**
     * 存储验证码到 Redis
     *
     * @param captchaId  验证码的唯一 ID（UUID 生成）
     * @param captcha    验证码文本（4位数字）
     *
     * 实际存到 Redis 的效果：
     *   SET ticket:captcha:abc123 "4567" EX 300
     *   300 秒（5 分钟）后自动删除
     */
    public void storeCaptcha(String captchaId, String captcha) {
        redisTemplate.opsForValue().set(
                CAPTCHA_PREFIX + captchaId,  // key：ticket:captcha:xxx
                captcha,                       // value：验证码内容
                CAPTCHA_EXPIRE,                // 过期时间长度
                TimeUnit.MINUTES               // 单位：分钟
        );
    }

    /**
     * 手动删除验证码
     * 一般情况下不需要手动调，因为过期时间会自动删除
     *
     * @param captchaId 验证码 ID
     */
    public void removeCaptcha(String captchaId) {
        redisTemplate.delete(CAPTCHA_PREFIX + captchaId);
    }

    /**
     * 校验验证码是否正确
     *
     * @param captchaId  验证码 ID
     * @param captcha    用户输入的验证码
     * @return true=验证通过  false=验证失败
     *
     * 流程：
     * 1. 从 Redis 取出之前存的验证码
     *    → GET ticket:captcha:xxx
     * 2. 如果取到的值和用户输入的一样
     *    → 删除这个 key（一次性使用，防止重复利用）
     *    → 返回 true
     * 3. 如果不一样或者 key 已过期
     *    → 返回 false
     */
    public Boolean validateCaptcha(String captchaId, String captcha) {
        String key = CAPTCHA_PREFIX + captchaId;

        // 从 Redis 获取存储的验证码
        String stored = redisTemplate.opsForValue().get(key);

        if (stored != null && stored.equals(captcha)) {
            // 验证码正确 → 立即删除（一次性使用）
            // 为什么用完要删？防止有人截获验证码 ID 重复使用
            redisTemplate.delete(key);
            return true;
        }

        return false;
    }
}
