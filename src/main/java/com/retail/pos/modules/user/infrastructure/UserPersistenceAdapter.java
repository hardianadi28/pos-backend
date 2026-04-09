package com.retail.pos.modules.user.infrastructure;

import com.retail.pos.modules.user.domain.User;
import com.retail.pos.modules.user.usecase.port.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {

    private final JpaUserRepository userRepository;

    @Override
    public List<User> findByRoles(Collection<String> roleNames) {
        return userRepository.findByRoleNameIn(roleNames).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity savedEntity = userRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id).map(this::toDomain);
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .roleId(user.getRoleId())
                .username(user.getUsername())
                .passwordHash(user.getPasswordHash())
                .pinHash(user.getPinHash())
                .name(user.getName())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .roleId(entity.getRoleId())
                .username(entity.getUsername())
                .passwordHash(entity.getPasswordHash())
                .pinHash(entity.getPinHash())
                .name(entity.getName())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
