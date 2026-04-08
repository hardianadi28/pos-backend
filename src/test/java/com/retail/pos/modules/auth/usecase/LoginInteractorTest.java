package com.retail.pos.modules.auth.usecase;

import com.retail.pos.core.security.JwtService;
import com.retail.pos.modules.auth.adapter.LoginRequest;
import com.retail.pos.modules.auth.adapter.LoginResponse;
import com.retail.pos.modules.user.domain.Role;
import com.retail.pos.modules.user.domain.User;
import com.retail.pos.modules.user.usecase.port.RolePort;
import com.retail.pos.modules.user.usecase.port.UserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class LoginInteractorTest {

    @Mock
    private UserPort userPort;
    @Mock
    private RolePort rolePort;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginInteractor loginInteractor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoginSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("admin")
                .passwordHash("hashed_password")
                .roleId(roleId)
                .isActive(true)
                .build();

        Role role = Role.builder()
                .id(roleId)
                .name("ADMIN")
                .permissions(Collections.singletonList("USER_CREATE"))
                .build();

        when(userPort.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed_password")).thenReturn(true);
        when(rolePort.findById(roleId)).thenReturn(Optional.of(role));
        when(jwtService.generateToken(anyString(), anyString(), anyMap())).thenReturn("test_token");

        LoginRequest request = LoginRequest.builder()
                .username("admin")
                .password("password")
                .build();

        LoginResponse response = loginInteractor.login(request);

        assertNotNull(response);
        assertEquals("test_token", response.getToken());
        assertNotNull(response.getExpiresAt());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userPort.findByUsername("unknown")).thenReturn(Optional.empty());

        LoginRequest request = LoginRequest.builder()
                .username("unknown")
                .password("password")
                .build();

        assertThrows(RuntimeException.class, () -> loginInteractor.login(request));
    }

    @Test
    void shouldThrowExceptionWhenPasswordInvalid() {
        User user = User.builder()
                .username("admin")
                .passwordHash("hashed_password")
                .isActive(true)
                .build();

        when(userPort.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed_password")).thenReturn(false);

        LoginRequest request = LoginRequest.builder()
                .username("admin")
                .password("wrong")
                .build();

        assertThrows(RuntimeException.class, () -> loginInteractor.login(request));
    }
}
