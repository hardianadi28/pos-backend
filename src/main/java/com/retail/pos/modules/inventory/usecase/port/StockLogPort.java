package com.retail.pos.modules.inventory.usecase.port;

import com.retail.pos.modules.inventory.domain.StockLog;

import java.util.List;
import java.util.UUID;

public interface StockLogPort {
    StockLog save(StockLog stockLog);
    List<StockLog> findLogsByProductId(UUID productId);
}
