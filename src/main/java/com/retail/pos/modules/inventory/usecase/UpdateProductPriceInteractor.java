package com.retail.pos.modules.inventory.usecase;

import com.retail.pos.modules.inventory.domain.Product;
import com.retail.pos.modules.inventory.domain.exception.InvalidPriceException;
import com.retail.pos.modules.inventory.domain.exception.ProductNotFoundException;
import com.retail.pos.modules.inventory.usecase.port.ProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateProductPriceInteractor implements UpdateProductPriceUseCase {

    private final ProductPort productPort;

    @Override
    @Transactional
    public Product execute(UUID productId, BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPriceException("Price cannot be negative");
        }

        Product product = productPort.findProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        product.setBasePrice(newPrice);
        return productPort.save(product);
    }
}
