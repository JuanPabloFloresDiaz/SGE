package com.auditory.auditory.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;
    private String userId;
    private String action;
    private String endpoint;
    private String ipAddress;
    private String device;
    private String requestBody;
    private Instant timestamp;
}
