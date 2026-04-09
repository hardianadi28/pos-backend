package com.retail.pos.modules.inventory.usecase.port;

import com.retail.pos.modules.inventory.domain.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryPort {
    Optional<Category> findCategoryById(UUID id);
}
