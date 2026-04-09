package com.retail.pos.modules.inventory.usecase.port;

import com.retail.pos.modules.inventory.domain.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductPort {
    Product save(Product product);
    Optional<Product> findProductById(UUID id);
    boolean existsBySku(String sku);
    boolean existsByBarcode(String barcode);
}
