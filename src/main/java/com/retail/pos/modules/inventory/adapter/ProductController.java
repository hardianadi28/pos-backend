package com.retail.pos.modules.inventory.adapter;

import com.retail.pos.core.web.response.WebResponse;
import com.retail.pos.modules.inventory.domain.Product;
import com.retail.pos.modules.inventory.usecase.CreateProductCommand;
import com.retail.pos.modules.inventory.usecase.CreateProductUseCase;
import com.retail.pos.modules.inventory.usecase.UpdateProductPriceUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductPriceUseCase updateProductPriceUseCase;

    @PostMapping
    public ResponseEntity<WebResponse<Product>> create(@Valid @RequestBody ProductRequest request) {
        CreateProductCommand command = CreateProductCommand.builder()
                .categoryId(request.getCategoryId())
                .sku(request.getSku())
                .barcode(request.getBarcode())
                .name(request.getName())
                .uom(request.getUom())
                .basePrice(request.getBasePrice())
                .build();

        Product product = createProductUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.success(product, "Product created successfully"));
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<WebResponse<Product>> updatePrice(
            @PathVariable UUID id,
            @RequestBody Map<String, BigDecimal> payload
    ) {
        BigDecimal newPrice = payload.get("new_price");
        Product product = updateProductPriceUseCase.execute(id, newPrice);
        return ResponseEntity.ok(WebResponse.success(product, "Product price updated successfully"));
    }
}
