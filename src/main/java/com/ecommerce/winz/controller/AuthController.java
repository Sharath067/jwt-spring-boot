package com.ecommerce.winz.controller;

import com.ecommerce.winz.dto.*;
import com.ecommerce.winz.model.User;
import com.ecommerce.winz.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody RegisterRequestDTO requestDTO) {
        User user = authService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponseDTO("User registered successfully! with Id : " + user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody LoginRequestDTO requestDTO) {
        JwtResponseDTO jwtResponse = authService.login(requestDTO);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO dto){
        authService.forgotPassword(dto);
        return ResponseEntity.ok("Password reset successfully");
    }

}
