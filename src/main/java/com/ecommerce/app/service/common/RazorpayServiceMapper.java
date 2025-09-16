package com.ecommerce.app.service.common;

import com.ecommerce.app.exception.type.ResourceException;
import com.ecommerce.app.util.Constants;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RazorpayServiceMapper {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.api.key.id}")
    private String keyID;

    @Value("${razorpay.api.key.secret}")
    private String keySecret;

    public String createPayment(Double amount, String receiptName, String tenantId) {
        try {
            int amountInPaise = BigDecimal.valueOf(amount)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();

            JSONObject orderRequest = new JSONObject()
                    .put("amount", amountInPaise)
                    .put("currency", "INR")
                    .put("receipt", receiptName + tenantId);

            log.info("OrderRequest: {}", orderRequest);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            String orderId = razorpayOrder.get("id");
            log.info("Razorpay order created successfully. Order ID: {}", orderId);

            return orderId;
        } catch (Exception e) {
            log.error("Create Payment: Error while creating payment. Message: {}", e.getMessage(), e);
            throw new ResourceException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    Constants.RAZORPAY_CREATE_PAYMENT_ERR_MSG
            );
        }
    }

    public boolean verifyPayment(String razorPayOrderId, String razorPayPaymentId, String razorPaySignature) {
        String generatedSignature = generateRazorpaySignature(razorPayOrderId, razorPayPaymentId, keySecret);
        return generatedSignature != null && generatedSignature.equals(razorPaySignature);
    }

    public String generateRazorpaySignature(String razorPayOrderId, String razorPayPaymentId, String keySecret) {
        String signature = null;
        try {
            String signatureData = razorPayOrderId + "|" + razorPayPaymentId;
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
            sha256HMAC.init(secretKey);

            byte[] bytes = sha256HMAC.doFinal(signatureData.getBytes());

            StringBuilder builder = new StringBuilder();
            for (byte aByte : bytes) {
                builder.append(String.format("%02x", aByte));
            }

            signature = builder.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.debug("Exception while generating signature {}", e.getMessage());
        }
        return signature;
    }
}
