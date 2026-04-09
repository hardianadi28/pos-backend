package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.Product;
import com.retail.pos.modules.inventory.domain.StockBatch;
import com.retail.pos.modules.inventory.domain.StockLog;
import com.retail.pos.modules.inventory.domain.exception.ProductNotFoundException;
import com.retail.pos.modules.inventory.usecase.port.ProductPort;
import com.retail.pos.modules.inventory.usecase.port.StockBatchPort;
import com.retail.pos.modules.inventory.usecase.port.StockLogPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboundStockInteractorTest {

    @Mock
    private ProductPort productPort;

    @Mock
    private StockBatchPort stockBatchPort;

    @Mock
    private StockLogPort stockLogPort;

    @InjectMocks
    private InboundStockInteractor inboundStockInteractor;

    @Test
    void execute_Success() {
        UUID productId = UUID.randomUUID();
        InboundStockCommand command = InboundStockCommand.builder()
                .productId(productId)
                .batchNumber("BATCH-001")
                .qty(100)
                .costPrice(BigDecimal.valueOf(5000))
                .expiryDate(OffsetDateTime.now().plusYears(1))
                .build();

        Product product = Product.builder().id(productId).build();
        when(productPort.findProductById(productId)).thenReturn(Optional.of(product));
        when(stockBatchPort.save(any(StockBatch.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stockBatchPort.findBatchesByProductId(productId)).thenReturn(Collections.singletonList(
                StockBatch.builder().qty(100).build()
        ));

        StockBatch result = inboundStockInteractor.execute(command);

        assertNotNull(result);
        assertEquals(command.getQty(), result.getQty());
        verify(stockBatchPort, times(1)).save(any(StockBatch.class));
        verify(stockLogPort, times(1)).save(any(StockLog.class));
    }

    @Test
    void execute_ProductNotFound() {
        UUID productId = UUID.randomUUID();
        InboundStockCommand command = InboundStockCommand.builder()
                .productId(productId)
                .build();

        when(productPort.findProductById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> inboundStockInteractor.execute(command));
        verifyNoInteractions(stockBatchPort);
        verifyNoInteractions(stockLogPort);
    }
}
