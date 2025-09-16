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
public class TenantPaymentResponse extends SuccessResponse {

    private String razorpayOrderId;

    private LocalDateTime paymentInitiatedAt;

    public TenantPaymentResponse(String id, LocalDateTime startTime, String message, int statusCode) {
        super(message, statusCode);
        this.razorpayOrderId = id;
        this.paymentInitiatedAt = startTime;
    }

}
