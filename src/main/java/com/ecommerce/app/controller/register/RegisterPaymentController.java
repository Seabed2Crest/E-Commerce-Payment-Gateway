package com.ecommerce.app.controller.register;

import com.ecommerce.app.request.register.RegisterPaymentRequest;
import com.ecommerce.app.request.register.VerifyRegisterRequest;
import com.ecommerce.app.response.ApiResponse;
import com.ecommerce.app.response.register.RegisterPaymentResponse;
import com.ecommerce.app.response.register.VerifyPaymentResponse;
import com.ecommerce.app.service.servicelayer.register.RegisterPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class RegisterPaymentController {

    private final RegisterPaymentService registerPaymentService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterPaymentResponse>> paymentRegister(@Valid @RequestBody RegisterPaymentRequest request) {
        ApiResponse<RegisterPaymentResponse> apiResponse = registerPaymentService.paymentRegister(request);
        return ResponseEntity.status(HttpStatus.OK.value()).body(apiResponse);
    }

    @PostMapping("/verifyRegister")
    public ResponseEntity<ApiResponse<VerifyPaymentResponse>> verifyRegister(@Valid @RequestBody VerifyRegisterRequest request) {
        ApiResponse<VerifyPaymentResponse> apiResponse = registerPaymentService.verifyRegister(request);
        return ResponseEntity.status(HttpStatus.OK.value()).body(apiResponse);
    }
}
