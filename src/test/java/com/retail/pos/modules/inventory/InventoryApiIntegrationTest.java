package com.retail.pos.modules.inventory;

import com.retail.pos.BaseIntegrationTest;
import com.retail.pos.core.security.JwtService;
import com.retail.pos.modules.inventory.adapter.InboundRequest;
import com.retail.pos.modules.inventory.adapter.ProductRequest;
import com.retail.pos.modules.user.infrastructure.JpaRoleRepository;
import com.retail.pos.modules.user.infrastructure.JpaUserRepository;
import com.retail.pos.modules.user.infrastructure.RoleEntity;
import com.retail.pos.modules.user.infrastructure.UserEntity;
import com.retail.pos.modules.inventory.infrastructure.JpaCategoryRepository;
import com.retail.pos.modules.inventory.infrastructure.CategoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryApiIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaRoleRepository roleRepository;

    @Autowired
    private JpaCategoryRepository categoryRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String cashierToken;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        categoryRepository.deleteAll();

        // Setup Role
        UUID adminRoleId = UUID.randomUUID();
        roleRepository.save(RoleEntity.builder()
                .id(adminRoleId)
                .name("ADMIN")
                .permissions(List.of("INVENTORY_ALL"))
                .createdAt(OffsetDateTime.now())
                .build());

        UUID cashierRoleId = UUID.randomUUID();
        roleRepository.save(RoleEntity.builder()
                .id(cashierRoleId)
                .name("CASHIER")
                .permissions(List.of("ORDER_CREATE"))
                .createdAt(OffsetDateTime.now())
                .build());

        // Setup Admin
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
        adminToken = "Bearer " + jwtService.generateToken(adminId.toString(), "admin", Map.of("role", "ADMIN", "permissions", List.of("INVENTORY_ALL")));

        // Setup Cashier
        UUID cashierId = UUID.randomUUID();
        userRepository.save(UserEntity.builder()
                .id(cashierId)
                .username("cashier")
                .passwordHash(passwordEncoder.encode("password"))
                .pinHash(passwordEncoder.encode("111111"))
                .name("Cashier")
                .roleId(cashierRoleId)
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .build());
        cashierToken = "Bearer " + jwtService.generateToken(cashierId.toString(), "cashier", Map.of("role", "CASHIER", "permissions", List.of("ORDER_CREATE")));

        // Setup Category
        categoryId = UUID.randomUUID();
        categoryRepository.save(CategoryEntity.builder()
                .id(categoryId)
                .name("Food")
                .build());
    }

    @Test
    void productAndInventoryLifecycle_Success() throws Exception {
        // 1. Create Product
        ProductRequest productRequest = ProductRequest.builder()
                .categoryId(categoryId)
                .sku("SKU-001")
                .barcode("BAR-001")
                .name("Susu UHT")
                .uom("PCS")
                .basePrice(BigDecimal.valueOf(15000))
                .build();

        String productResponse = mockMvc.perform(post("/api/v1/products")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Susu UHT"))
                .andReturn().getResponse().getContentAsString();

        UUID productId = UUID.fromString(objectMapper.readTree(productResponse).get("data").get("id").asText());

        // 2. Update Price
        mockMvc.perform(patch("/api/v1/products/" + productId + "/price")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("new_price", BigDecimal.valueOf(16000)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.basePrice").value(16000));

        // 3. Inbound Stock
        InboundRequest inboundRequest = InboundRequest.builder()
                .productId(productId)
                .batchNumber("BATCH-A1")
                .qty(50)
                .costPrice(BigDecimal.valueOf(12000))
                .expiryDate(OffsetDateTime.now().plusMonths(6).toString())
                .build();

        mockMvc.perform(post("/api/v1/inventory/inbound")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inboundRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.qty").value(50));
    }

    @Test
    void stockOpname_AndPagination_Success() throws Exception {
        // 1. Setup Product and Stock
        ProductRequest productRequest = ProductRequest.builder()
                .categoryId(categoryId).sku("SKU-OP").barcode("BAR-OP").name("Opname Test").uom("PCS").basePrice(BigDecimal.valueOf(1000)).build();
        String productResponse = mockMvc.perform(post("/api/v1/products").header("Authorization", adminToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        UUID productId = UUID.fromString(objectMapper.readTree(productResponse).get("data").get("id").asText());

        InboundRequest inboundRequest = InboundRequest.builder().productId(productId).batchNumber("B1").qty(100).costPrice(BigDecimal.valueOf(800)).expiryDate(OffsetDateTime.now().plusDays(30).toString()).build();
        String inboundResponse = mockMvc.perform(post("/api/v1/inventory/inbound").header("Authorization", adminToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inboundRequest)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        UUID batchId = UUID.fromString(objectMapper.readTree(inboundResponse).get("data").get("id").asText());

        // 2. Stock Opname - Success with valid PIN
        mockMvc.perform(post("/api/v1/inventory/stock-opname")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "batch_id", batchId,
                                "physical_qty", 95,
                                "reason", "Damaged",
                                "manager_pin", "123456"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.qty").value(95));

        // 3. Stock Opname - Fail with invalid PIN
        mockMvc.perform(post("/api/v1/inventory/stock-opname")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "batch_id", batchId,
                                "physical_qty", 90,
                                "reason", "Damaged",
                                "manager_pin", "wrong_pin"
                        ))))
                .andExpect(status().isUnauthorized());

        // 4. Get Logs with Pagination
        mockMvc.perform(get("/api/v1/inventory/logs/" + productId + "?page=0&size=1")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2)); // 1 Inbound + 1 Success Adjustment
    }

    @Test
    void access_ForbiddenForCashier() throws Exception {
        ProductRequest productRequest = ProductRequest.builder()
                .name("Should Fail")
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .header("Authorization", cashierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isForbidden());
    }
}
