package com.auditory.auditory.service;

import com.auditory.auditory.model.AuditLog;
import com.auditory.auditory.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void saveLog(AuditLog log) {
        auditLogRepository.save(log);
    }
}
