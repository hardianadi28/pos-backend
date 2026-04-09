package com.retail.pos.modules.inventory.infrastructure;

import com.retail.pos.modules.inventory.domain.Category;
import com.retail.pos.modules.inventory.domain.Product;
import com.retail.pos.modules.inventory.domain.StockBatch;
import com.retail.pos.modules.inventory.domain.StockLog;
import com.retail.pos.modules.inventory.usecase.port.CategoryPort;
import com.retail.pos.modules.inventory.usecase.port.ProductPort;
import com.retail.pos.modules.inventory.usecase.port.StockBatchPort;
import com.retail.pos.modules.inventory.usecase.port.StockLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InventoryPersistenceAdapter implements CategoryPort, ProductPort, StockBatchPort, StockLogPort {

    private final JpaCategoryRepository categoryRepository;
    private final JpaProductRepository productRepository;
    private final JpaStockBatchRepository stockBatchRepository;
    private final JpaStockLogRepository stockLogRepository;

    // --- Category ---
    @Override
    public Optional<Category> findCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(entity -> Category.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .build());
    }

    // --- Product ---
    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductEntity.builder()
                .id(product.getId())
                .categoryId(product.getCategoryId())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .name(product.getName())
                .uom(product.getUom())
                .basePrice(product.getBasePrice())
                .build();
        productRepository.save(entity);
        return product;
    }

    @Override
    public Optional<Product> findProductById(UUID id) {
        return productRepository.findById(id)
                .map(entity -> Product.builder()
                        .id(entity.getId())
                        .categoryId(entity.getCategoryId())
                        .sku(entity.getSku())
                        .barcode(entity.getBarcode())
                        .name(entity.getName())
                        .uom(entity.getUom())
                        .basePrice(entity.getBasePrice())
                        .build());
    }

    @Override
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    @Override
    public boolean existsByBarcode(String barcode) {
        return productRepository.existsByBarcode(barcode);
    }

    // --- StockBatch ---
    @Override
    public StockBatch save(StockBatch stockBatch) {
        StockBatchEntity entity = StockBatchEntity.builder()
                .id(stockBatch.getId())
                .productId(stockBatch.getProductId())
                .batchNumber(stockBatch.getBatchNumber())
                .qty(stockBatch.getQty())
                .costPrice(stockBatch.getCostPrice())
                .expiryDate(stockBatch.getExpiryDate())
                .build();
        stockBatchRepository.save(entity);
        return stockBatch;
    }

    @Override
    public List<StockBatch> findBatchesByProductId(UUID productId) {
        return stockBatchRepository.findByProductId(productId).stream()
                .map(entity -> StockBatch.builder()
                        .id(entity.getId())
                        .productId(entity.getProductId())
                        .batchNumber(entity.getBatchNumber())
                        .qty(entity.getQty())
                        .costPrice(entity.getCostPrice())
                        .expiryDate(entity.getExpiryDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<StockBatch> findExpiringSoon(OffsetDateTime thresholdDate) {
        return stockBatchRepository.findByExpiryDateBefore(thresholdDate).stream()
                .map(entity -> StockBatch.builder()
                        .id(entity.getId())
                        .productId(entity.getProductId())
                        .batchNumber(entity.getBatchNumber())
                        .qty(entity.getQty())
                        .costPrice(entity.getCostPrice())
                        .expiryDate(entity.getExpiryDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StockBatch> findBatchById(UUID id) {
        return stockBatchRepository.findById(id)
                .map(entity -> StockBatch.builder()
                        .id(entity.getId())
                        .productId(entity.getProductId())
                        .batchNumber(entity.getBatchNumber())
                        .qty(entity.getQty())
                        .costPrice(entity.getCostPrice())
                        .expiryDate(entity.getExpiryDate())
                        .build());
    }

    // --- StockLog ---
    @Override
    public StockLog save(StockLog stockLog) {
        StockLogEntity entity = StockLogEntity.builder()
                .id(stockLog.getId())
                .productId(stockLog.getProductId())
                .batchId(stockLog.getBatchId())
                .type(stockLog.getType())
                .qtyChange(stockLog.getQtyChange())
                .balance(stockLog.getBalance())
                .refId(stockLog.getRefId())
                .createdAt(stockLog.getCreatedAt())
                .build();
        stockLogRepository.save(entity);
        return stockLog;
    }

    @Override
    public Page<StockLog> findLogsByProductId(UUID productId, int page, int size) {
        return stockLogRepository.findByProductId(productId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(entity -> StockLog.builder()
                        .id(entity.getId())
                        .productId(entity.getProductId())
                        .batchId(entity.getBatchId())
                        .type(entity.getType())
                        .qtyChange(entity.getQtyChange())
                        .balance(entity.getBalance())
                        .refId(entity.getRefId())
                        .createdAt(entity.getCreatedAt())
                        .build());
    }
}
