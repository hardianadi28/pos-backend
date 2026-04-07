package com.retail.pos.modules.user.usecase;

import com.retail.pos.modules.user.domain.User;
import com.retail.pos.modules.user.infrastructure.JpaRoleRepository;
import com.retail.pos.modules.user.infrastructure.JpaUserRepository;
import com.retail.pos.modules.user.infrastructure.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUserInteractor implements RegisterUserUseCase {

    private final JpaUserRepository userRepository;
    private final JpaRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public User execute(RegisterUserCommand command) {
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (!roleRepository.existsById(command.getRoleId())) {
            throw new RuntimeException("Role not found");
        }

        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .roleId(command.getRoleId())
                .username(command.getUsername())
                .passwordHash(passwordEncoder.encode(command.getPassword()))
                .pinHash(passwordEncoder.encode(command.getPin()))
                .name(command.getName())
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        userRepository.save(userEntity);

        return User.builder()
                .id(userEntity.getId())
                .roleId(userEntity.getRoleId())
                .username(userEntity.getUsername())
                .name(userEntity.getName())
                .isActive(userEntity.getIsActive())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .build();
    }
}
