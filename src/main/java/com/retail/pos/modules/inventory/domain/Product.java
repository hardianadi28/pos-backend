package com.retail.pos.modules.inventory.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private UUID id;
    private UUID categoryId;
    private String sku;
    private String barcode;
    private String name;
    private String uom;
    private BigDecimal basePrice;
}
