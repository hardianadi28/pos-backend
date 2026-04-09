package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.Product;
import com.retail.pos.modules.inventory.domain.exception.CategoryNotFoundException;
import com.retail.pos.modules.inventory.domain.exception.DuplicateProductException;
import com.retail.pos.modules.inventory.usecase.port.CategoryPort;
import com.retail.pos.modules.inventory.usecase.port.ProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateProductInteractor implements CreateProductUseCase {

    private final ProductPort productPort;
    private final CategoryPort categoryPort;

    @Override
    @Transactional
    public Product execute(CreateProductCommand command) {
        if (!categoryPort.findCategoryById(command.getCategoryId()).isPresent()) {
            throw new CategoryNotFoundException("Category not found with id: " + command.getCategoryId());
        }

        if (productPort.existsBySku(command.getSku())) {
            throw new DuplicateProductException("Product with SKU " + command.getSku() + " already exists");
        }

        if (productPort.existsByBarcode(command.getBarcode())) {
            throw new DuplicateProductException("Product with Barcode " + command.getBarcode() + " already exists");
        }

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .categoryId(command.getCategoryId())
                .sku(command.getSku())
                .barcode(command.getBarcode())
                .name(command.getName())
                .uom(command.getUom())
                .basePrice(command.getBasePrice())
                .build();

        return productPort.save(product);
    }
}
