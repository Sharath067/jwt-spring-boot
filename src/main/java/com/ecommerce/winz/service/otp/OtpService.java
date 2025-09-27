package com.ecommerce.winz.service.otp;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpService {
    private final StringRedisTemplate stringRedisTemplate;
    private final SecureRandom random = new SecureRandom();

    private int otpMinute;

    public OtpService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
}
