package com.ecommerce.app.service.serviceimplementation.token;

import com.ecommerce.app.exception.type.ResourceException;
import com.ecommerce.app.request.token.AuthTokenRequest;
import com.ecommerce.app.response.ApiResponse;
import com.ecommerce.app.response.token.AuthTokenResponse;
import com.ecommerce.app.service.servicelayer.token.AuthTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthTokenServiceImplementation implements AuthTokenService {

    private static final String ZONE_ID = "Asia/Kolkata";

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.tenant-token-expiration}")
    private long tenantTokenExpiration;


    @Override
    public ApiResponse<AuthTokenResponse> generateTenantPaymentToken(AuthTokenRequest request) {
        log.info("Auth Token Request: {}", request);
        ZonedDateTime issuedAtZoned =  ZonedDateTime.now(ZoneId.of(ZONE_ID));
        ZonedDateTime expiredAtZoned = issuedAtZoned.plusSeconds(tenantTokenExpiration);

        Date issuedAt = Date.from(issuedAtZoned.toInstant());
        Date expiresAt = Date.from(expiredAtZoned.toInstant());

        String token = generateToken(request, issuedAt, expiresAt);
        AuthTokenResponse response = new AuthTokenResponse(
                token,
                issuedAtZoned,
                expiredAtZoned
        );
        log.info("Auth Token Response: {}", response);
        return ApiResponse.success(response);
    }

    @Override
    public String extractCustomerId(String token) {
        Claims claims = parseClaimsJwsToken(token);
        return claims.get("sub", String.class);
    }

    @Override
    public boolean isTokenExpired(String token) {
        Claims claims = parseClaimsJwsToken(token);
        return claims.getExpiration().before(new Date());
    }

    private String generateToken(AuthTokenRequest request, Date issuedAt, Date expiresAt) {
        return Jwts.builder()
                .setSubject(request.getClientId())
                .setClaims(request.getCustomClaims())
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(getSignInKey())
                .compact();
    }

    private Claims parseClaimsJwsToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    "Invalid JWT token");
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
