package com.ecommerce.app.response.register;

import com.ecommerce.app.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyPaymentResponse extends SuccessResponse {

    private String razorpayPaymentId;

    public VerifyPaymentResponse(String id, String message, int statusCode) {
        super(message, statusCode);
        this.razorpayPaymentId = id;
    }
}
