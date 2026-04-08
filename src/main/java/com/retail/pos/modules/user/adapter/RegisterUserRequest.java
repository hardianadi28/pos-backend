package com.retail.pos.modules.user.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String pin;

    @NotBlank
    private String name;

    @NotNull
    @JsonProperty("role_id")
    private UUID roleId;

    @Override
    public String toString() {
        return "RegisterUserRequest(" +
                "username='" + username + '\'' +
                ", password='****'" +
                ", pin='****'" +
                ", name='" + name + '\'' +
                ", roleId=" + roleId +
                ')';
    }
}
