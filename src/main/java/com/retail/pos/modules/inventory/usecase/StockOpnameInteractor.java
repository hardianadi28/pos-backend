package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.StockBatch;
import com.retail.pos.modules.inventory.domain.StockLog;
import com.retail.pos.modules.inventory.domain.exception.StockBatchNotFoundException;
import com.retail.pos.modules.inventory.domain.exception.UnauthorizedManagerException;
import com.retail.pos.modules.inventory.usecase.port.StockBatchPort;
import com.retail.pos.modules.inventory.usecase.port.StockLogPort;
import com.retail.pos.modules.user.domain.User;
import com.retail.pos.modules.user.usecase.port.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockOpnameInteractor implements StockOpnameUseCase {

    private final StockBatchPort stockBatchPort;
    private final StockLogPort stockLogPort;
    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public StockBatch execute(StockOpnameCommand command) {
        StockBatch batch = stockBatchPort.findBatchById(command.getBatchId())
                .orElseThrow(() -> new StockBatchNotFoundException("Stock batch not found with id: " + command.getBatchId()));

        // 1. Verify Manager PIN
        List<User> managers = userPort.findByRoles(Arrays.asList("ADMIN", "SUPERVISOR"));
        boolean pinValid = managers.stream()
                .anyMatch(user -> passwordEncoder.matches(command.getManagerPin(), user.getPinHash()));

        if (!pinValid) {
             throw new UnauthorizedManagerException("Invalid Manager PIN");
        }

        int qtyChange = command.getPhysicalQty() - batch.getQty();
        if (qtyChange == 0) return batch;

        // 2. Update Batch Qty
        batch.setQty(command.getPhysicalQty());
        stockBatchPort.save(batch);

        // 3. Calculate New Balance
        int currentBalance = stockBatchPort.findBatchesByProductId(batch.getProductId()).stream()
                .mapToInt(StockBatch::getQty)
                .sum();

        // 4. Create Stock Log
        StockLog log = StockLog.builder()
                .id(UUID.randomUUID())
                .productId(batch.getProductId())
                .batchId(batch.getId())
                .type("ADJUSTMENT")
                .qtyChange(qtyChange)
                .balance(currentBalance)
                .refId("OPNAME-" + UUID.randomUUID().toString().substring(0, 8))
                .createdAt(OffsetDateTime.now())
                .build();
        
        stockLogPort.save(log);

        return batch;
    }
}
