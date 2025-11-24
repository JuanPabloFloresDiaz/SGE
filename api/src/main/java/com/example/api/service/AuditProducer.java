package com.example.api.service;

import com.example.api.dto.AuditLogDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditProducer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuditProducer(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendAuditLog(AuditLogDTO log) {
        try {
            String message = objectMapper.writeValueAsString(log);
            redisTemplate.convertAndSend("audit-channel", message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
