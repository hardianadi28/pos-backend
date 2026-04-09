package com.retail.pos.modules.inventory.usecase;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class StockOpnameCommand {
    UUID batchId;
    Integer physicalQty;
    String reason;
    String managerPin;
}
