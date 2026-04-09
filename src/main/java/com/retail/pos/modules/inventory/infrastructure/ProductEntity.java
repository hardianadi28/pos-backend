package com.retail.pos.modules.inventory.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    private UUID id;
    
    @Column(name = "category_id")
    private UUID categoryId;
    
    @Column(unique = true)
    private String sku;
    
    @Column(unique = true)
    private String barcode;
    
    private String name;
    private String uom;
    
    @Column(name = "base_price")
    private BigDecimal basePrice;
}
