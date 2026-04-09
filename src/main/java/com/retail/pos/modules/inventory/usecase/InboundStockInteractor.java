package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.StockBatch;
import com.retail.pos.modules.inventory.domain.StockLog;
import com.retail.pos.modules.inventory.domain.exception.ProductNotFoundException;
import com.retail.pos.modules.inventory.usecase.port.ProductPort;
import com.retail.pos.modules.inventory.usecase.port.StockBatchPort;
import com.retail.pos.modules.inventory.usecase.port.StockLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InboundStockInteractor implements InboundStockUseCase {

    private final ProductPort productPort;
    private final StockBatchPort stockBatchPort;
    private final StockLogPort stockLogPort;

    @Override
    @Transactional
    public StockBatch execute(InboundStockCommand command) {
        if (!productPort.findProductById(command.getProductId()).isPresent()) {
            throw new ProductNotFoundException("Product not found with id: " + command.getProductId());
        }

        // 1. Create Stock Batch
        StockBatch batch = StockBatch.builder()
                .id(UUID.randomUUID())
                .productId(command.getProductId())
                .batchNumber(command.getBatchNumber())
                .qty(command.getQty())
                .costPrice(command.getCostPrice())
                .expiryDate(command.getExpiryDate())
                .build();
        
        StockBatch savedBatch = stockBatchPort.save(batch);

        // 2. Calculate Balance
        int currentBalance = stockBatchPort.findBatchesByProductId(command.getProductId()).stream()
                .mapToInt(StockBatch::getQty)
                .sum();

        // 3. Create Stock Log
        StockLog log = StockLog.builder()
                .id(UUID.randomUUID())
                .productId(command.getProductId())
                .batchId(savedBatch.getId())
                .type("INBOUND")
                .qtyChange(command.getQty())
                .balance(currentBalance)
                .refId(command.getBatchNumber()) // Or use a specific PO number if available
                .createdAt(OffsetDateTime.now())
                .build();
        
        stockLogPort.save(log);

        return savedBatch;
    }
}
