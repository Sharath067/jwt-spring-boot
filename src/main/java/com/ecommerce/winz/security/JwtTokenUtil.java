package com.ecommerce.winz.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenUtil {

    private final Key accessTokenKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final Key refreshTokenKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private final long accessTokenValidityInMinutes = 60;
    private final long refreshTokenValidityInMinutes = 10080;

    public String generateAccessToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidityInMinutes * 60 * 1000))
                .signWith(accessTokenKey)
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidityInMinutes * 60 * 1000))
                .signWith(refreshTokenKey)
                .compact();
    }

    public Claims getClaimsFromToken(String token, boolean isAccessToken) {
        Key key = isAccessToken ? accessTokenKey : refreshTokenKey;
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
