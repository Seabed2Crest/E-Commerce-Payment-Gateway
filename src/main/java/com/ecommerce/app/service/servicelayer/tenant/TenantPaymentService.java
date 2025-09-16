package com.ecommerce.app.service.servicelayer.tenant;

import com.ecommerce.app.request.tenant.PaymentRequest;
import com.ecommerce.app.request.tenant.VerifyPaymentRequest;
import com.ecommerce.app.response.ApiResponse;
import com.ecommerce.app.response.tenant.TenantPaymentResponse;
import com.ecommerce.app.response.tenant.TenantVerifyPaymentResponse;

public interface TenantPaymentService {

    ApiResponse<TenantPaymentResponse> tenantCreatePayment(PaymentRequest paymentRequest);

    ApiResponse<TenantVerifyPaymentResponse> tenantVerifyPayment(VerifyPaymentRequest paymentRequest);
}
