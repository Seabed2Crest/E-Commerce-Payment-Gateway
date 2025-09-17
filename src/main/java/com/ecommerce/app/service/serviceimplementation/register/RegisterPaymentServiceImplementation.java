package com.ecommerce.app.service.serviceimplementation.register;

import com.ecommerce.app.entity.publicschema.SubscriptionPaymentEntity;
import com.ecommerce.app.entity.publicschema.TenantSubscriptionEntity;
import com.ecommerce.app.enums.PaymentStatus;
import com.ecommerce.app.enums.ServiceType;
import com.ecommerce.app.exception.type.ResourceException;
import com.ecommerce.app.repository.publicschema.TenantSubscriptionRepository;
import com.ecommerce.app.request.register.RegisterPaymentRequest;
import com.ecommerce.app.request.register.VerifyRegisterRequest;
import com.ecommerce.app.response.ApiResponse;
import com.ecommerce.app.response.register.RegisterPaymentResponse;
import com.ecommerce.app.response.register.VerifyPaymentResponse;
import com.ecommerce.app.schema.TenantSchemaService;
import com.ecommerce.app.service.common.RazorpayServiceMapper;
import com.ecommerce.app.service.servicelayer.register.RegisterPaymentService;
import com.ecommerce.app.util.Constants;
import com.ecommerce.app.util.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterPaymentServiceImplementation implements RegisterPaymentService {

    private final TenantSubscriptionRepository tenantSubscriptionRepository;

    private final Generator generator;

    private final RazorpayServiceMapper razorpayServiceMapper;

    private final TenantSchemaService tenantSchemaService;

    public static final String RAZORPAY_PAYMENT_GATEWAY = "Razorpay Payment Gateway";

    @Transactional
    @Override
    public ApiResponse<RegisterPaymentResponse> paymentRegister(RegisterPaymentRequest request) {
        try {
            log.info("Payment Register Request : {}", request);
            TenantSubscriptionEntity entity = tenantSubscriptionRepository.findByTenantId(request.getTenantId()).orElseGet(() -> mapToTenantSubscriptionEntity(request));

            SubscriptionPaymentEntity newPayment = mapToSubscriptionPaymentEntity(request, entity);
            newPayment.setPaymentInitiatedAt(LocalDateTime.now(ZoneId.of(Constants.ZONE_ID)));

            String razorpayOrderId = razorpayServiceMapper.createPayment(request.getSubscriptionAmount(), "subscription_payment_receipt_", request.getTenantId());
            newPayment.setRazorPayOrderId(razorpayOrderId);

            entity.getSubscriptionPayments().add(newPayment);
            tenantSubscriptionRepository.save(entity);

            RegisterPaymentResponse response = new RegisterPaymentResponse(razorpayOrderId, Constants.CREATE_PAYMENT_SUCCESS_MSG, HttpStatus.OK.value());

            log.info("Payment Register Response: {}", response);
            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("Payment Register: Error occurred while payment creation : {}", e.getMessage());
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name(), Constants.PAYMENT_REGISTER_ERR_MSG);
        }
    }

    @Transactional
    @Override
    public ApiResponse<VerifyPaymentResponse> verifyRegister(VerifyRegisterRequest request) {
        try {
            log.info("Verify Register Request: {}", request);

            TenantSubscriptionEntity entity = tenantSubscriptionRepository.findByTenantIdAndSubscriptionPaymentsRazorPayOrderId(request.getTenantId(), request.getRazorPayOrderId()).orElseThrow(() -> {
                log.error("Verify Register: Payment account not found for razorpay order id: {}", request.getRazorPayOrderId());
                return new ResourceException(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.name(), Constants.ACCOUNT_NOT_FOUND_ERR_MSG);
            });

            SubscriptionPaymentEntity latestPayment = entity.getSubscriptionPayments().stream().filter(p -> request.getRazorPayOrderId().equals(p.getRazorPayOrderId())).findFirst().orElseThrow(() -> new ResourceException(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.toString(), Constants.PAYMENT_VERIFICATION_ERR_MSG));

            boolean isSuccess = razorpayServiceMapper.verifyPayment(request.getRazorPayOrderId(), request.getRazorPayPaymentId(), request.getRazorPaySignature());

            latestPayment.setRazorPayPaymentId(request.getRazorPayPaymentId());
            latestPayment.setPaymentCompletedAt(LocalDateTime.now(ZoneId.of(Constants.ZONE_ID)));

            if (isSuccess) {
                latestPayment.setPaymentStatus(PaymentStatus.SUCCESS.name());
                entity.setIsSubscriptionActive(true);
                if (entity.getServicesEnabled().contains(RAZORPAY_PAYMENT_GATEWAY) && entity.getSchemaName() != null) {
                    tenantSchemaService.createSchema(entity.getSchemaName());
                    log.info("Tenant schema created successfully : {}", entity.getSchemaName());
                }

            } else {
                latestPayment.setPaymentStatus(PaymentStatus.FAILED.name());
                entity.setIsSubscriptionActive(false);
                log.warn("Payment verification failed for orderId: {}", request.getRazorPayOrderId());
            }
            tenantSubscriptionRepository.save(entity);

            String message = isSuccess ? Constants.PAYMENT_SUCCESS_MSG : Constants.PAYMENT_FAILED_MSG;
            int statusCode = isSuccess ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value();

            VerifyPaymentResponse response = new VerifyPaymentResponse(latestPayment.getRazorPayPaymentId(), message, statusCode);
            log.info("Verify Payment Response : {}", response);
            return ApiResponse.success(response);

        } catch (ResourceException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Exception during Razorpay verification for orderId {}: {}", request.getRazorPayOrderId(), e.getMessage(), e);
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name(), Constants.PAYMENT_VERIFICATION_FAILED_ERR_MSG);

        }
    }

    private TenantSubscriptionEntity mapToTenantSubscriptionEntity(RegisterPaymentRequest request) {
        TenantSubscriptionEntity entity = new TenantSubscriptionEntity();
        entity.setTenantSubscriptionId(generator.generateId(Constants.TENANT_SUBSCRIPTION_ID));
        entity.setTenantId(request.getTenantId());
        entity.setCompanyName(request.getCompanyName());
        entity.setUserName(request.getUserName());
        entity.setEmailAddress(request.getEmailAddress());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setSchemaName(request.getTenantId().toLowerCase().replaceAll("\\s+", "_"));
        entity.setServicesEnabled(request.getServicesEnabled());
        entity.setIsAutoPaymentEnabled(request.getIsAutoPaymentEnabled());
        entity.setBusinessType(request.getBusinessType());
        entity.setSchemaName("payment_" + request.getCompanyName()
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "_"));
        return entity;
    }

    private SubscriptionPaymentEntity mapToSubscriptionPaymentEntity(RegisterPaymentRequest request, TenantSubscriptionEntity tenantSubscription) {
        SubscriptionPaymentEntity entity = new SubscriptionPaymentEntity();
        entity.setSubscriptionPaymentId(generator.generateId(Constants.SUBSCRIPTION_PAYMENT_ID));
        entity.setSubscriptionAmount(request.getSubscriptionAmount());
        entity.setServicePeriod(ServiceType.valueOf(request.getServicePeriod()));
        entity.setServiceStartDate(request.getServiceStartDate());
        entity.setServiceEndDate(request.getServiceEndDate());
        entity.setPaymentStatus(PaymentStatus.CREATED.toString());
        entity.setTenantSubscription(tenantSubscription);
        return entity;
    }
}
