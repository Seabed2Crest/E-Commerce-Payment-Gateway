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
public class RegisterPaymentResponse extends SuccessResponse {

    private String razorPayOrderId;

    public RegisterPaymentResponse(String razorPayOrderId, String message, int statusCode) {
        super(message, statusCode);
        this.razorPayOrderId = razorPayOrderId;
    }

}
