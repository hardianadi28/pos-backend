package com.retail.pos.modules.auth;

import com.retail.pos.BaseIntegrationTest;
import com.retail.pos.modules.auth.adapter.LoginRequest;
import com.retail.pos.modules.user.infrastructure.JpaRoleRepository;
import com.retail.pos.modules.user.infrastructure.JpaUserRepository;
import com.retail.pos.modules.user.infrastructure.RoleEntity;
import com.retail.pos.modules.user.infrastructure.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiTest extends BaseIntegrationTest {

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        UUID roleId = UUID.randomUUID();
        roleRepository.save(RoleEntity.builder()
                .id(roleId)
                .name("ADMIN")
                .permissions(List.of("USER_CREATE"))
                .createdAt(OffsetDateTime.now())
                .build());

        userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .passwordHash(passwordEncoder.encode("password123"))
                .pinHash(passwordEncoder.encode("123456"))
                .name("Admin")
                .roleId(roleId)
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .build());

        userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .username("inactive")
                .passwordHash(passwordEncoder.encode("password123"))
                .pinHash(passwordEncoder.encode("123456"))
                .name("Inactive")
                .roleId(roleId)
                .isActive(false)
                .createdAt(OffsetDateTime.now())
                .build());
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("admin")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.expiresAt").exists());
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("admin")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void login_UserInactive() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("inactive")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User is inactive"));
    }
}
