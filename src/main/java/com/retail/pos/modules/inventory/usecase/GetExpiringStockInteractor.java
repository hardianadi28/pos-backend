package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.StockBatch;
import com.retail.pos.modules.inventory.usecase.port.StockBatchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetExpiringStockInteractor {

    private final StockBatchPort stockBatchPort;

    public List<StockBatch> execute(int daysThreshold) {
        OffsetDateTime thresholdDate = OffsetDateTime.now().plusDays(daysThreshold);
        
        // This is a naive implementation, ideally we'd have a custom query in repository
        // But for MVP and since we only have small amount of data in this context:
        // Actually, I'll add a method to StockBatchPort if I wanted to be efficient.
        // For now, I'll just filter in memory as a simple implementation.
        // Wait, I should probably add it to the port to be more professional.
        return stockBatchPort.findExpiringSoon(thresholdDate);
    }
}
