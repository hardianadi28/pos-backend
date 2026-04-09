package com.retail.pos.modules.inventory.usecase;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class InboundStockCommand {
    UUID productId;
    String batchNumber;
    Integer qty;
    BigDecimal costPrice;
    OffsetDateTime expiryDate;
}
