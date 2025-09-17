package com.ecommerce.app.service.common;

import com.ecommerce.app.exception.type.ResourceException;
import com.ecommerce.app.util.Constants;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class RazorpayServiceMapper {

    private final RazorpayClient razorpayClient;

    private final HttpClient client = HttpClient.newHttpClient();

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

    public String sendHttpRequest(String url, JSONObject payload, HttpMethod httpMethod, String errorMessage) {
        try {
            log.info("Sending request to URL: {}, Payload: {}", url, payload);
            String auth = Base64.getEncoder().encodeToString((keyID + ":" + keySecret).getBytes());

            HttpRequest.BodyPublisher bodyPublisher = payload != null
                    ? HttpRequest.BodyPublishers.ofString(payload.toString())
                    : HttpRequest.BodyPublishers.noBody();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "application/json")
                    .method(String.valueOf(httpMethod), bodyPublisher)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201 && response.statusCode() != 200) {
                log.error("{} Status Code: {}, Response Body: {}", errorMessage, response.statusCode(), response.body());
                throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.name(), errorMessage);
            }

            log.info("Response: {}", response.body());
            return response.body();

        } catch (HttpTimeoutException timeoutException) {
            log.error(Constants.RAZORPAY_CONTACT_TIMEOUT, timeoutException);
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    Constants.RAZORPAY_CONTACT_TIMEOUT);

        } catch (IOException ioException) {
            log.error(Constants.RAZORPAY_CONTACT_IO_ERROR, ioException);
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    Constants.RAZORPAY_CONTACT_IO_ERROR);

        } catch (InterruptedException ie) {
            log.error(Constants.RAZORPAY_CONTACT_TIMEOUT, ie);
            Thread.currentThread().interrupt();
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    Constants.RAZORPAY_CONTACT_TIMEOUT);

        } catch (Exception e) {
            log.error(Constants.RAZORPAY_CONTACT_UNEXPECTED_ERROR, e);
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    Constants.RAZORPAY_CONTACT_UNEXPECTED_ERROR);
        }
    }
}
