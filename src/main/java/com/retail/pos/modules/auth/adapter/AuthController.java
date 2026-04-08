package com.retail.pos.modules.auth.adapter;

import com.retail.pos.core.web.response.WebResponse;
import com.retail.pos.modules.auth.usecase.LoginInteractor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginInteractor loginInteractor;

    @PostMapping("/login")
    public ResponseEntity<WebResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginInteractor.login(request);
        return ResponseEntity.ok(WebResponse.success(response, "Login successful"));
    }
}
