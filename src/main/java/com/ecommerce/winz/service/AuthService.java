package com.ecommerce.winz.service;

import com.ecommerce.winz.dto.JwtResponseDTO;
import com.ecommerce.winz.dto.LoginRequestDTO;
import com.ecommerce.winz.dto.LoginResponseDTO;
import com.ecommerce.winz.dto.RegisterRequestDTO;
import com.ecommerce.winz.exception.BadRequestException;
import com.ecommerce.winz.exception.ConflictException;
import com.ecommerce.winz.exception.ResourceNotFoundException;
import com.ecommerce.winz.exception.UnauthorizedException;
import com.ecommerce.winz.model.User;
import com.ecommerce.winz.repository.UserRepository;
import com.ecommerce.winz.security.JwtTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public User register(RegisterRequestDTO requestDTO){
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists!"+requestDTO.getEmail());
        } else if (userRepository.findByUserName(requestDTO.getUserName()).isPresent()) {
            throw new ConflictException(("User name already exists!"+requestDTO.getUserName()));
        }

        if(requestDTO.getPassword() == null || requestDTO.getPassword().length() < 6 ) {
            throw new BadRequestException("Password must be atleast 6 characters");
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
        User user = userRepository.findByEmailOrUserName(
                requestDTO.getEmailOrUserName(), requestDTO.getEmailOrUserName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with : "+requestDTO.getEmailOrUserName()));

        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Claims for JWT
        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put("ROLE", "ADMIN");
        tokenClaims.put("USERID", user.getId());
        tokenClaims.put("sub", "user");

        String accessToken = jwtTokenUtil.generateAccessToken(tokenClaims);
        String refreshToken = jwtTokenUtil.generateRefreshToken(tokenClaims);

        return new JwtResponseDTO(
                accessToken,
                60,
                refreshToken,
                10080
        );
    }

}
