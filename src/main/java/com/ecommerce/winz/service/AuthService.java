package com.ecommerce.winz.service;

import com.ecommerce.winz.dto.*;
import com.ecommerce.winz.exception.BadRequestException;
import com.ecommerce.winz.exception.ConflictException;
import com.ecommerce.winz.exception.ResourceNotFoundException;
import com.ecommerce.winz.exception.UnauthorizedException;
import com.ecommerce.winz.model.User;
import com.ecommerce.winz.repository.UserRepository;
import com.ecommerce.winz.security.JwtTokenUtil;
import com.ecommerce.winz.service.email.EmailService;
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
    private final EmailService emailService;


    public User register(RegisterRequestDTO requestDTO){

        String email = requestDTO.getEmail().trim().toLowerCase();
        String username = requestDTO.getUserName().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already exists!");
        }

        if (userRepository.findByUserName(username).isPresent()) {
            throw new ConflictException("User name already exists!");
        }

        if(requestDTO.getPassword() == null || requestDTO.getPassword().length() < 6 ) {
            throw new BadRequestException("Password must be atleast 6 characters");
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        User user = User.builder()
                .userName(username)
                .email(email)
                .password(encodedPassword)
                .build();

        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getUserName());

        return user;
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

    public void forgotPassword(ForgotPasswordDTO request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with this email "+request.getEmail()));

        if(request.getNewPassword() == null || request.getNewPassword().length() < 6){
            throw new BadRequestException("Password must be at least 6 characters");
        }

        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new BadRequestException("New password and confirm password do not match");
        }

        String newPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(newPassword);

        userRepository.save(user);
        emailService.sendResetPasswordEmail(
                user.getEmail(),
                user.getUserName(),
                request.getNewPassword()
        );
    }

}
