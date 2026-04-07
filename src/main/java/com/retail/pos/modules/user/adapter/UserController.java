package com.retail.pos.modules.user.adapter;

import com.retail.pos.core.web.response.WebResponse;
import com.retail.pos.modules.user.domain.User;
import com.retail.pos.modules.user.usecase.RegisterUserCommand;
import com.retail.pos.modules.user.usecase.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<WebResponse<User>> register(@RequestBody RegisterUserRequest request) {
        RegisterUserCommand command = RegisterUserCommand.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .pin(request.getPin())
                .name(request.getName())
                .roleId(request.getRole_id())
                .build();

        User user = registerUserUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.success(user, "User registered successfully"));
    }
}
