package com.ecommerce.app.util;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.UUID;

@Component
public class Generator {

    public String generateId(String generatorId) {
        UUID uuid = UUID.randomUUID();
        String numericUUID = new BigInteger(uuid.toString().replace("-", ""), 16).toString();
        String generatedId = switch (generatorId) {
            case Constants.SUBSCRIPTION_PAYMENT_ID, Constants.TENANT_PAYMENT_ID -> numericUUID.substring(0, 6);
            case null, default -> numericUUID;
        };
        return generatorId + generatedId;
    }
}
