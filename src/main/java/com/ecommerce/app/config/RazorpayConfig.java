package com.ecommerce.app.config;

import com.payment.app.exception.internalservererror.ResourceInternalServerErrorException;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@Getter
@Setter
public class RazorpayConfig {

    @Value("${razorpay.api.key.id}")
    private String keyID;

    @Value("${razorpay.api.key.secret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        if (keyID == null || keySecret == null) {
            throw new ResourceInternalServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    "Razorpay key ID or secret is not configured.");
        }
        return new RazorpayClient(keyID, keySecret);
    }
}
