package com.ecommerce.app.response.tenant;

import com.ecommerce.app.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantVerifyPaymentResponse extends SuccessResponse {

    private String razorpayPaymentId;

    private LocalDateTime paymentCompletedAt;

    public TenantVerifyPaymentResponse(String id, LocalDateTime completedAt, String message, int statusCode) {
        super(message, statusCode);
        this.razorpayPaymentId = id;
        this.paymentCompletedAt = completedAt;
    }
}
