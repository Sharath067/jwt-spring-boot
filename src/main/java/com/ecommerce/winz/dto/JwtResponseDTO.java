package com.ecommerce.winz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String accessToken;
    private long accessTokenExpireIn;
    private String refreshToken;
    private long refreshTokenExpireIn;
}
