package com.ecommerce.app.controller.token;

import com.ecommerce.app.request.token.AuthTokenRequest;
import com.ecommerce.app.response.ApiResponse;
import com.ecommerce.app.response.token.AuthTokenResponse;
import com.ecommerce.app.service.servicelayer.token.AuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthTokenController {

    private final AuthTokenService authTokenService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> generateTenantPaymentToken(@RequestBody AuthTokenRequest request) {
        ApiResponse<AuthTokenResponse> apiResponse = authTokenService.generateTenantPaymentToken(request);
        return ResponseEntity.status(HttpStatus.OK.value()).body(apiResponse);
    }
}
