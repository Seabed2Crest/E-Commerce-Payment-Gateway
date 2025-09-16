package com.ecommerce.app.service.serviceimplementation.tenant;

import com.ecommerce.app.entity.tenantschema.TenantCustomerPaymentEntity;
import com.ecommerce.app.enums.PaymentStatus;
import com.ecommerce.app.exception.type.ResourceException;
import com.ecommerce.app.repository.tenantschema.TenantPaymentRepository;
import com.ecommerce.app.request.tenant.PaymentRequest;
import com.ecommerce.app.request.tenant.VerifyPaymentRequest;
import com.ecommerce.app.response.ApiResponse;
import com.ecommerce.app.response.tenant.TenantPaymentResponse;
import com.ecommerce.app.response.tenant.TenantVerifyPaymentResponse;
import com.ecommerce.app.service.common.RazorpayServiceMapper;
import com.ecommerce.app.service.servicelayer.tenant.TenantPaymentService;
import com.ecommerce.app.util.Constants;
import com.ecommerce.app.util.Generator;
import com.ecommerce.app.validations.annotationvalidator.TenantTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantPaymentServiceImplementation implements TenantPaymentService {

    private final TenantPaymentRepository tenantPaymentRepository;

    private final Generator generator;

    private final RazorpayServiceMapper razorpayServiceMapper;

    @TenantTransactional
    @Override
    public ApiResponse<TenantPaymentResponse> tenantCreatePayment(PaymentRequest paymentRequest) {
        try {
            log.info("Tenant Create Payment Request: {}", paymentRequest);
            TenantCustomerPaymentEntity entity = mapDetailsToTenantPayment(paymentRequest);
            entity.setPaymentInitiatedAt(LocalDateTime.now(ZoneId.of(Constants.ZONE_ID)));
            String razorpayOrderId = razorpayServiceMapper.createPayment(paymentRequest.getTotalAmount(), "tenant_customer_payment_receipt", paymentRequest.getTenantId());
            entity.setRazorPayOrderId(razorpayOrderId);
            tenantPaymentRepository.save(entity);

            TenantPaymentResponse response = new TenantPaymentResponse(
                    razorpayOrderId, entity.getPaymentInitiatedAt(),
                    Constants.CREATE_PAYMENT_SUCCESS_MSG, HttpStatus.OK.value()
            );

            log.info("Tenant Create Payment Response: {}", response);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("Tenant Create Payment : Error message: {}", e.getMessage());
            throw new ResourceException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    Constants.PAYMENT_REGISTER_ERR_MSG
            );
        }
    }

    @TenantTransactional
    @Override
    public ApiResponse<TenantVerifyPaymentResponse> tenantVerifyPayment(VerifyPaymentRequest paymentRequest) {
        try {
            log.info("Tenant Verify Payment Request: {}", paymentRequest);
            TenantCustomerPaymentEntity entity = tenantPaymentRepository.findByTenantIdAndRazorPayOrderId(paymentRequest.getTenantId(), paymentRequest.getRazorPayOrderId());
            if (isNull(entity)) {
                log.error("Tenant Verify Payment: Payment account not found for the razorpay order id : {} and customer id: {}", paymentRequest.getRazorPayPaymentId(), paymentRequest.getTenantId());
                throw new ResourceException(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.toString(),
                        Constants.ACCOUNT_NOT_FOUND_ERR_MSG
                );
            }

            boolean isSuccess = razorpayServiceMapper.verifyPayment(paymentRequest.getRazorPayOrderId(), paymentRequest.getRazorPayPaymentId(), paymentRequest.getRazorPaySignature());

            if (isSuccess) {
                entity.setPaymentStatus(PaymentStatus.SUCCESS);
                entity.setPaymentCompletedAt(LocalDateTime.now(ZoneId.of(Constants.ZONE_ID)));
                log.info("Payment verified successfully for orderId: {}", paymentRequest.getRazorPayOrderId());

            } else {
                entity.setPaymentStatus(PaymentStatus.FAILED);
                entity.setPaymentCompletedAt(LocalDateTime.now(ZoneId.of(Constants.ZONE_ID)));
                log.warn("Payment verification failed for orderId: {}", paymentRequest.getRazorPayOrderId());
            }

            entity.setRazorPayPaymentId(paymentRequest.getRazorPayPaymentId());
            tenantPaymentRepository.save(entity);

            String message = isSuccess ? Constants.PAYMENT_SUCCESS_MSG : Constants.PAYMENT_FAILED_MSG;
            int statusCode = isSuccess ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value();

            TenantVerifyPaymentResponse paymentResponse = new TenantVerifyPaymentResponse(
                    entity.getRazorPayPaymentId(), entity.getPaymentCompletedAt(), message, statusCode);
            log.info("Tenant Verify Payment Response : {}", paymentResponse);
            return ApiResponse.success(paymentResponse);
        } catch (ResourceException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Exception during Razorpay verification for orderId {}: {}", paymentRequest.getRazorPayOrderId(), e.getMessage(), e);
            throw new ResourceException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    Constants.PAYMENT_VERIFICATION_FAILED_ERR_MSG
            );
        }
    }

    private TenantCustomerPaymentEntity mapDetailsToTenantPayment(PaymentRequest paymentRequest) {
        TenantCustomerPaymentEntity entity = new TenantCustomerPaymentEntity();
        entity.setTenantPaymentId(generator.generateId(Constants.TENANT_PAYMENT_ID));
        entity.setTenantId(paymentRequest.getTenantId());
        entity.setCustomerName(paymentRequest.getCustomerName());
        entity.setCustomerEmailAddress(paymentRequest.getCustomerEmailAddress());
        entity.setPhoneNumber(paymentRequest.getPhoneNumber());
        entity.setOrderNumberId(paymentRequest.getOrderNumberId());
        entity.setTotalAmount(paymentRequest.getTotalAmount());
        entity.setPaymentMethod(paymentRequest.getPaymentMethod());
        return entity;
    }
}
