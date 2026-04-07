package com.retail.pos.modules.user.infrastructure;

import com.retail.pos.modules.user.domain.Role;
import com.retail.pos.modules.user.usecase.port.RolePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RolePort {

    private final JpaRoleRepository roleRepository;

    @Override
    public boolean existsById(UUID id) {
        return roleRepository.existsById(id);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return roleRepository.findById(id).map(entity -> Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .permissions(entity.getPermissions())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build());
    }
}
