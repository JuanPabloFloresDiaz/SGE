package com.example.api.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class AuditLogDTO {
    private String userId;
    private String action;
    private String endpoint;
    private String ipAddress;
    private String device;
    private String requestBody;
    private Instant timestamp;
}
