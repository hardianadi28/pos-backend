package com.retail.pos.modules.inventory.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockLog {
    private UUID id;
    private UUID productId;
    private UUID batchId;
    private String type; // SALE, INBOUND, VOID, ADJUSTMENT, RETURN
    private Integer qtyChange;
    private Integer balance;
    private String refId;
    private OffsetDateTime createdAt;
}
