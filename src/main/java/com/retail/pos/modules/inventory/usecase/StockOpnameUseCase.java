package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.StockBatch;

public interface StockOpnameUseCase {
    StockBatch execute(StockOpnameCommand command);
}
