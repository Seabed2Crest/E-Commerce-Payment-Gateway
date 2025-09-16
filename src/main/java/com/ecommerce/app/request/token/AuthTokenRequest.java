package com.ecommerce.app.request.token;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenRequest {

    @NotBlank(message = "Client id is required")
    private String clientId;

    private Map<String, Object> customClaims = new HashMap<>();
}
