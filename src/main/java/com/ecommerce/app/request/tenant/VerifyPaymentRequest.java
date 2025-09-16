package com.ecommerce.app.request.tenant;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyPaymentRequest {

    private String tenantId;

    @NotBlank(message = "Razorpay order ID is required")
    private String razorPayOrderId;

    @NotBlank(message = "Razorpay payment ID is required")
    private String razorPayPaymentId;

    @NotBlank(message = "Razorpay signature is required")
    private String razorPaySignature;
}
