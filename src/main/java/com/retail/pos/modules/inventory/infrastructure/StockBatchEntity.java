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
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock_batches")
public class StockBatchEntity {
    @Id
    private UUID id;
    
    @Column(name = "product_id")
    private UUID productId;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    private Integer qty;
    
    @Column(name = "cost_price")
    private BigDecimal costPrice;
    
    @Column(name = "expiry_date")
    private OffsetDateTime expiryDate;
}
