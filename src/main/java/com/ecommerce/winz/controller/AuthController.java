package com.ecommerce.winz.controller;

import com.ecommerce.winz.dto.JwtResponseDTO;
import com.ecommerce.winz.dto.LoginRequestDTO;
import com.ecommerce.winz.dto.LoginResponseDTO;
import com.ecommerce.winz.dto.RegisterRequestDTO;
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
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody RegisterRequestDTO requestDTO){
        try{
            User user = authService.register(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new LoginResponseDTO("User registered successfully! with Id : "+user.getId()));
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponseDTO(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody LoginRequestDTO requestDTO) {
        try {
            JwtResponseDTO jwtResponse = authService.login(requestDTO);
            return ResponseEntity.ok(jwtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO requestDTO) {
//        try{
//            User user = authService.login(requestDTO);
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(new LoginResponseDTO("Login successful! Welcome " + user.getUserName()));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new LoginResponseDTO(e.getMessage()));
//        }
//    }
}
