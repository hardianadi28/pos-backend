package com.retail.pos.modules.inventory.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface JpaStockLogRepository extends JpaRepository<StockLogEntity, UUID> {
    List<StockLogEntity> findByProductId(UUID productId);
}
