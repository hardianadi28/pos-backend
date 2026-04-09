package com.retail.pos.modules.inventory.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {
    boolean existsBySku(String sku);
    boolean existsByBarcode(String barcode);
}
