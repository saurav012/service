package com.weather.service.service;

import com.weather.model.WeatherResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisCacheImplementation {

    private final RedisTemplate<String, WeatherResponse> redisTemplate;


    public RedisCacheImplementation(RedisTemplate<String, WeatherResponse> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, WeatherResponse value, Long ttl) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttl));
    }

    public WeatherResponse get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }


}
