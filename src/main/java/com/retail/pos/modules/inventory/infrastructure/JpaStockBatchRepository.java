package com.retail.pos.modules.inventory.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaStockBatchRepository extends JpaRepository<StockBatchEntity, UUID> {
    List<StockBatchEntity> findByProductId(UUID productId);
    List<StockBatchEntity> findByExpiryDateBefore(OffsetDateTime date);
}
