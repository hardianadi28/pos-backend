package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.Product;

public interface CreateProductUseCase {
    Product execute(CreateProductCommand command);
}
