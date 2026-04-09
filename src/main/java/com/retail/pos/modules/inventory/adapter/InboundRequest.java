package com.retail.pos.modules.inventory.adapter;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class InboundRequest {
    UUID productId;
    String batchNumber;
    Integer qty;
    BigDecimal costPrice;
    String expiryDate; // Using String for ISO format handling in controller
}
