package com.retail.pos.modules.user;

import com.retail.pos.BaseIntegrationTest;
import com.retail.pos.core.security.JwtService;
import com.retail.pos.modules.user.adapter.RegisterUserRequest;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserApiTest extends BaseIntegrationTest {

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaRoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String cashierToken;
    private UUID cashierRoleId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // 1. Setup Roles
        UUID adminRoleId = UUID.randomUUID();
        roleRepository.save(RoleEntity.builder()
                .id(adminRoleId)
                .name("ADMIN")
                .permissions(List.of("USER_CREATE"))
                .createdAt(OffsetDateTime.now())
                .build());

        cashierRoleId = UUID.randomUUID();
        roleRepository.save(RoleEntity.builder()
                .id(cashierRoleId)
                .name("CASHIER")
                .permissions(List.of("ORDER_CREATE"))
                .createdAt(OffsetDateTime.now())
                .build());

        // 2. Setup Admin User
        UUID adminId = UUID.randomUUID();
        userRepository.save(UserEntity.builder()
                .id(adminId)
                .username("admin")
                .passwordHash(passwordEncoder.encode("password"))
                .pinHash(passwordEncoder.encode("123456"))
                .name("Admin")
                .roleId(adminRoleId)
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .build());

        adminToken = "Bearer " + jwtService.generateToken(adminId.toString(), "admin", Map.of("role", "ADMIN", "permissions", List.of("USER_CREATE")));

        // 3. Setup Cashier User
        UUID cashierId = UUID.randomUUID();
        userRepository.save(UserEntity.builder()
                .id(cashierId)
                .username("cashier")
                .passwordHash(passwordEncoder.encode("password"))
                .pinHash(passwordEncoder.encode("654321"))
                .name("Cashier")
                .roleId(cashierRoleId)
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .build());

        cashierToken = "Bearer " + jwtService.generateToken(cashierId.toString(), "cashier", Map.of("role", "CASHIER", "permissions", List.of("ORDER_CREATE")));
    }

    @Test
    void registerUser_Success() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("newuser")
                .password("password123")
                .pin("123456")
                .name("New User")
                .roleId(cashierRoleId)
                .build();

        mockMvc.perform(post("/api/v1/users/register")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void registerUser_Forbidden_InsufficientRole() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("newuser")
                .password("password123")
                .pin("123456")
                .name("New User")
                .roleId(cashierRoleId)
                .build();

        mockMvc.perform(post("/api/v1/users/register")
                        .header("Authorization", cashierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void registerUser_Unauthorized_NoToken() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("newuser")
                .password("password123")
                .roleId(cashierRoleId)
                .build();

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Spring Security returns 403 for unauthenticated by default here
    }

    @Test
    void registerUser_Conflict_DuplicateUsername() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("admin") // Alredy exists from setUp
                .password("password123")
                .pin("123456")
                .name("Admin Clone")
                .roleId(cashierRoleId)
                .build();

        mockMvc.perform(post("/api/v1/users/register")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username already exists: admin"));
    }
}
