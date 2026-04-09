package com.retail.pos.modules.inventory.usecase.port;

import com.retail.pos.modules.inventory.domain.StockBatch;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockBatchPort {
    StockBatch save(StockBatch stockBatch);
    Optional<StockBatch> findBatchById(UUID id);
    List<StockBatch> findBatchesByProductId(UUID productId);
    List<StockBatch> findExpiringSoon(OffsetDateTime thresholdDate);
}
