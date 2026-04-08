package com.retail.pos.modules.user;

import com.retail.pos.modules.user.adapter.RegisterUserRequest;
import com.retail.pos.modules.user.usecase.RegisterUserCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogMaskingTest {

    @Test
    void shouldMaskSensitiveDataInRegisterUserRequest() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("test-user")
                .password("secret123")
                .pin("123456")
                .name("Test User")
                .roleId(UUID.randomUUID())
                .build();

        String toString = request.toString();
        assertFalse(toString.contains("secret123"));
        assertFalse(toString.contains("123456"));
        assertTrue(toString.contains("****"));
        assertTrue(toString.contains("test-user"));
    }

    @Test
    void shouldMaskSensitiveDataInRegisterUserCommand() {
        RegisterUserCommand command = RegisterUserCommand.builder()
                .username("test-user")
                .password("secret123")
                .pin("123456")
                .name("Test User")
                .roleId(UUID.randomUUID())
                .build();

        String toString = command.toString();
        assertFalse(toString.contains("secret123"));
        assertFalse(toString.contains("123456"));
        assertTrue(toString.contains("****"));
        assertTrue(toString.contains("test-user"));
    }
}
