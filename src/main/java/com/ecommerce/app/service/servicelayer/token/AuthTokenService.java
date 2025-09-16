package com.ecommerce.app.service.servicelayer.token;


import com.ecommerce.app.request.token.AuthTokenRequest;
import com.ecommerce.app.response.ApiResponse;
import com.ecommerce.app.response.token.AuthTokenResponse;

public interface AuthTokenService {

    ApiResponse<AuthTokenResponse> generateTenantPaymentToken(AuthTokenRequest request);

    String extractCustomerId(String token);

    boolean isTokenExpired(String token);
}
