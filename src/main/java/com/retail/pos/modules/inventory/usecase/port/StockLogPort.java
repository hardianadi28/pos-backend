package com.retail.pos.modules.inventory.usecase.port;

import com.retail.pos.modules.inventory.domain.StockLog;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface StockLogPort {
    StockLog save(StockLog stockLog);
    Page<StockLog> findLogsByProductId(UUID productId, int page, int size);
}
