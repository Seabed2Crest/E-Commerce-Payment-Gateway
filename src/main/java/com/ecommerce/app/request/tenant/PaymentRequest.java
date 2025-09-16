package com.ecommerce.app.request.tenant;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private String tenantId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Invalid email format")
    private String customerEmailAddress;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Order number id is required")
    private String orderNumberId;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be greater than zero")
    private Double totalAmount;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "PRE-PAID|COD", message = "Payment method can be PRE-PAID or COD")
    private String paymentMethod;
}
