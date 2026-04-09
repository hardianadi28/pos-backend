package com.retail.pos.modules.inventory.usecase;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class CreateProductCommand {
    UUID categoryId;
    String sku;
    String barcode;
    String name;
    String uom;
    BigDecimal basePrice;
}
