package com.retail.pos.modules.user.usecase;

import com.retail.pos.modules.user.domain.User;
import com.retail.pos.modules.user.domain.exception.DuplicateUsernameException;
import com.retail.pos.modules.user.domain.exception.RoleNotFoundException;
import com.retail.pos.modules.user.usecase.port.RolePort;
import com.retail.pos.modules.user.usecase.port.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUserInteractor implements RegisterUserUseCase {

    private final UserPort userPort;
    private final RolePort rolePort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User execute(RegisterUserCommand command) {
        if (userPort.existsByUsername(command.getUsername())) {
            throw new DuplicateUsernameException("Username already exists: " + command.getUsername());
        }

        if (!rolePort.existsById(command.getRoleId())) {
            throw new RoleNotFoundException("Role not found with id: " + command.getRoleId());
        }

        User user = User.builder()
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

        return userPort.save(user);
    }
}
