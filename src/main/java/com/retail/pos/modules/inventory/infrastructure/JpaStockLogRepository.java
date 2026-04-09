package com.retail.pos.modules.inventory.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaStockLogRepository extends JpaRepository<StockLogEntity, UUID> {
    Page<StockLogEntity> findByProductId(UUID productId, Pageable pageable);
}
