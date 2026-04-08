package com.retail.pos.modules.user.usecase;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class RegisterUserCommand {
    private String username;
    private String password;
    private String pin;
    private String name;
    private UUID roleId;

    @Override
    public String toString() {
        return "RegisterUserCommand(" +
                "username='" + username + '\'' +
                ", password='****'" +
                ", pin='****'" +
                ", name='" + name + '\'' +
                ", roleId=" + roleId +
                ')';
    }
}
