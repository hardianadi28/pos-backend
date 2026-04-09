package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.StockLog;
import com.retail.pos.modules.inventory.usecase.port.StockLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetStockLogsInteractor {

    private final StockLogPort stockLogPort;

    public Page<StockLog> execute(UUID productId, int page, int size) {
        return stockLogPort.findLogsByProductId(productId, page, size);
    }
}
