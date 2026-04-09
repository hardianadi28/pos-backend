package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateProductPriceUseCase {
    Product execute(UUID productId, BigDecimal newPrice);
}
