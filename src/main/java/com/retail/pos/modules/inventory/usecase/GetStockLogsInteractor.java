package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.StockLog;
import com.retail.pos.modules.inventory.usecase.port.StockLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetStockLogsInteractor {

    private final StockLogPort stockLogPort;

    public List<StockLog> execute(UUID productId) {
        return stockLogPort.findLogsByProductId(productId);
    }
}
