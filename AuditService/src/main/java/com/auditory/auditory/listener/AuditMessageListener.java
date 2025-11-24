package com.auditory.auditory.listener;

import com.auditory.auditory.model.AuditLog;
import com.auditory.auditory.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuditMessageListener implements MessageListener {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuditMessageListener(AuditService auditService, ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            AuditLog log = objectMapper.readValue(message.getBody(), AuditLog.class);
            auditService.saveLog(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
