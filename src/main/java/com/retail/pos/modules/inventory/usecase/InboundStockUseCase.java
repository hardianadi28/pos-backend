package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.StockBatch;

public interface InboundStockUseCase {
    StockBatch execute(InboundStockCommand command);
}
