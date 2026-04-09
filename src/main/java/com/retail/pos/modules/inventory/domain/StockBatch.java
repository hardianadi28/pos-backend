package com.retail.pos.modules.inventory.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockBatch {
    private UUID id;
    private UUID productId;
    private String batchNumber;
    private Integer qty;
    private BigDecimal costPrice;
    private OffsetDateTime expiryDate;
}
