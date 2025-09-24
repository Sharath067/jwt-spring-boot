package com.ecommerce.winz.service;

import com.ecommerce.winz.dto.JwtResponseDTO;
import com.ecommerce.winz.dto.LoginRequestDTO;
import com.ecommerce.winz.dto.LoginResponseDTO;
import com.ecommerce.winz.dto.RegisterRequestDTO;
import com.ecommerce.winz.model.User;
import com.ecommerce.winz.repository.UserRepository;
import com.ecommerce.winz.security.JwtTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public User register(RegisterRequestDTO requestDTO){
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
        User user = User.builder()
                .userName(requestDTO.getUserName())
                .email(requestDTO.getEmail())
                .password(encodedPassword)
                .build();

        return userRepository.save(user);
    }

    public JwtResponseDTO login(LoginRequestDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Claims for JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("ROLE", "ADMIN");
        claims.put("USERID", user.getId());
        claims.put("sub", "user");

        String accessToken = jwtTokenUtil.generateAccessToken(claims);
        String refreshToken = jwtTokenUtil.generateRefreshToken(claims);

        return new JwtResponseDTO(
                accessToken,
                60,
                refreshToken,
                10080
        );
    }

//    public User login(LoginRequestDTO requestDTO){
//        User user = userRepository.findByEmail(requestDTO.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())){
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        return user;
//    }
}
