package com.retail.pos.modules.auth.usecase;

import com.retail.pos.core.security.JwtService;
import com.retail.pos.modules.auth.adapter.LoginRequest;
import com.retail.pos.modules.auth.adapter.LoginResponse;
import com.retail.pos.modules.auth.domain.exception.InvalidCredentialsException;
import com.retail.pos.modules.auth.domain.exception.UserInactiveException;
import com.retail.pos.modules.user.domain.Role;
import com.retail.pos.modules.user.domain.User;
import com.retail.pos.modules.user.usecase.port.RolePort;
import com.retail.pos.modules.user.usecase.port.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginInteractor {

    private final UserPort userPort;
    private final RolePort rolePort;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        User user = userPort.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!user.getIsActive()) {
            throw new UserInactiveException("User is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        Role role = rolePort.findById(user.getRoleId())
                .orElseThrow(() -> new RuntimeException("User role not found"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("role", role.getName());
        claims.put("permissions", role.getPermissions());

        String token = jwtService.generateToken(user.getId().toString(), user.getUsername(), claims);
        OffsetDateTime expiresAt = LocalDateTime.now().with(LocalTime.MAX).atOffset(ZoneOffset.UTC);

        return LoginResponse.builder()
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }
}
