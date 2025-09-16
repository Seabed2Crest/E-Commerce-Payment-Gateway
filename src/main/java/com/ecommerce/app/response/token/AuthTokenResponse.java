package com.ecommerce.app.response.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenResponse {

    private String token;

    private ZonedDateTime issuedAt;

    private ZonedDateTime expiresAt;
}
