package com.ecommerce.app.controller.tenant;

import com.ecommerce.app.exception.type.ResourceException;
import com.ecommerce.app.request.tenant.PaymentRequest;
import com.ecommerce.app.request.tenant.VerifyPaymentRequest;
import com.ecommerce.app.response.ApiResponse;
import com.ecommerce.app.response.tenant.TenantPaymentResponse;
import com.ecommerce.app.response.tenant.TenantVerifyPaymentResponse;
import com.ecommerce.app.service.servicelayer.tenant.TenantPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tenant")
@RequiredArgsConstructor
public class TenantPaymentController {

    private final TenantPaymentService tenantPaymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<ApiResponse<TenantPaymentResponse>> tenantCreatePayment(@Valid @RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        String tenantId = checkTenantId(request);
        paymentRequest.setTenantId(tenantId);
        ApiResponse<TenantPaymentResponse> apiResponse = tenantPaymentService.tenantCreatePayment(paymentRequest);
        return ResponseEntity.status(HttpStatus.OK.value()).body(apiResponse);
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<ApiResponse<TenantVerifyPaymentResponse>> verifyPayment(@Valid @RequestBody VerifyPaymentRequest paymentRequest, HttpServletRequest request) {
        String tenantId = checkTenantId(request);
        paymentRequest.setTenantId(tenantId);
        ApiResponse<TenantVerifyPaymentResponse> apiResponse = tenantPaymentService.tenantVerifyPayment(paymentRequest);
        return ResponseEntity.status(HttpStatus.OK.value()).body(apiResponse);
    }


    private String checkTenantId(HttpServletRequest request) {
        String customerId = (String) request.getAttribute("customer-id");
        if (StringUtils.isBlank(customerId)) {
            throw new ResourceException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    "Customer id is not present in request");
        }
        return customerId;
    }
}
