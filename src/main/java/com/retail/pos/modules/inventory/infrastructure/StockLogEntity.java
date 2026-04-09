package com.retail.pos.modules.inventory.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "stock_logs")
public class StockLogEntity {
    @Id
    private UUID id;
    
    @Column(name = "product_id")
    private UUID productId;
    
    @Column(name = "batch_id")
    private UUID batchId;
    
    private String type;
    
    @Column(name = "qty_change")
    private Integer qtyChange;
    
    private Integer balance;
    
    @Column(name = "ref_id")
    private String refId;
    
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
