package com.retail.pos.modules.user.usecase.port;

import com.retail.pos.modules.user.domain.Role;
import java.util.Optional;
import java.util.UUID;

public interface RolePort {
    boolean existsById(UUID id);
    Optional<Role> findById(UUID id);
}
