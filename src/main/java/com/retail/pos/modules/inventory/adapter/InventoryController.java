package com.retail.pos.modules.inventory.adapter;

import com.retail.pos.core.web.response.WebResponse;
import com.retail.pos.modules.inventory.domain.StockBatch;
import com.retail.pos.modules.inventory.domain.StockLog;
import com.retail.pos.modules.inventory.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InboundStockUseCase inboundStockUseCase;
    private final StockOpnameUseCase stockOpnameUseCase;
    private final GetExpiringStockInteractor getExpiringStockInteractor;
    private final GetStockLogsInteractor getStockLogsInteractor;

    @PostMapping("/inbound")
    public ResponseEntity<WebResponse<StockBatch>> inbound(@Valid @RequestBody InboundRequest request) {
        InboundStockCommand command = InboundStockCommand.builder()
                .productId(request.getProductId())
                .batchNumber(request.getBatchNumber())
                .qty(request.getQty())
                .costPrice(request.getCostPrice())
                .expiryDate(OffsetDateTime.parse(request.getExpiryDate()))
                .build();

        StockBatch result = inboundStockUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.success(result, "Stock inbound processed successfully"));
    }

    @PostMapping("/stock-opname")
    public ResponseEntity<WebResponse<StockBatch>> stockOpname(@Valid @RequestBody StockOpnameRequest request) {
        StockOpnameCommand command = StockOpnameCommand.builder()
                .batchId(request.getBatchId())
                .physicalQty(request.getPhysicalQty())
                .reason(request.getReason())
                .managerPin(request.getManagerPin())
                .build();

        StockBatch result = stockOpnameUseCase.execute(command);
        return ResponseEntity.ok(WebResponse.success(result, "Stock opname processed successfully"));
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<WebResponse<List<StockBatch>>> getExpiringSoon(@RequestParam(defaultValue = "30") int days) {
        List<StockBatch> result = getExpiringStockInteractor.execute(days);
        return ResponseEntity.ok(WebResponse.success(result, "Expiring stock retrieved successfully"));
    }

    @GetMapping("/logs/{productId}")
    public ResponseEntity<WebResponse<List<StockLog>>> getLogs(@PathVariable UUID productId) {
        List<StockLog> result = getStockLogsInteractor.execute(productId);
        return ResponseEntity.ok(WebResponse.success(result, "Stock logs retrieved successfully"));
    }
}
