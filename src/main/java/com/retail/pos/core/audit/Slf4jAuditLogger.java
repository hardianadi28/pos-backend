package com.retail.pos.core.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class Slf4jAuditLogger implements AuditLogger {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    @Override
    public void log(UUID userId, String action, String resourceId, Object oldValue, Object newValue) {
        // [Timestamp] [User_ID] [Action] [Resource_ID] [Old_Value] [New_Value]
        auditLog.info("{} | User: {} | Action: {} | Resource: {} | Old: {} | New: {}",
                OffsetDateTime.now(),
                userId != null ? userId : "SYSTEM",
                action,
                resourceId,
                oldValue,
                newValue);
    }
}
