package com.elite.controllers;

import com.elite.request.LoginRequest;
import com.elite.request.RegisterRequest;
import com.elite.request.UpdateUserRequest;
import com.elite.response.LoginResponse;
import com.elite.response.RegisterResponse;
import com.elite.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest req ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }


    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req ) {
        return ResponseEntity.ok(authService.login(req));

    }



    @GetMapping("/users/me")
    public ResponseEntity<LoginResponse> me() {
        return ResponseEntity.ok(authService.getMe());

    }


    @PutMapping("/users/{id}")
    public ResponseEntity<?> update( @RequestBody @Valid UpdateUserRequest req, @PathVariable String id ) {
        return  ResponseEntity.ok(authService.updateUserProfile(req, id));

    }
}
