package com.ecommerce.app.service.servicelayer.register;


import com.ecommerce.app.request.register.RegisterPaymentRequest;
import com.ecommerce.app.request.register.VerifyRegisterRequest;
import com.ecommerce.app.response.ApiResponse;
import com.ecommerce.app.response.register.RegisterPaymentResponse;
import com.ecommerce.app.response.register.VerifyPaymentResponse;

public interface RegisterPaymentService {

    ApiResponse<RegisterPaymentResponse> paymentRegister(RegisterPaymentRequest request);

    ApiResponse<VerifyPaymentResponse> verifyRegister(VerifyRegisterRequest request);
}
