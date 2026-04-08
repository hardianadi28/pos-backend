package com.retail.pos.core.audit;

import java.util.UUID;

public interface AuditLogger {
    void log(UUID userId, String action, String resourceId, Object oldValue, Object newValue);
}
