package com.ecommerce.app.request.register;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyRegisterRequest {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Razorpay order ID is required")
    private String razorPayOrderId;

    @NotBlank(message = "Razorpay payment ID is required")
    private String razorPayPaymentId;

    @NotBlank(message = "Razorpay signature is required")
    private String razorPaySignature;

}
