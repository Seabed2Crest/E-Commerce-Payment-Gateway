package com.ecommerce.app.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    PENDING("Pending"),
    CREATED("Created"),
    FAILED("Failed"),
    SUCCESS("Success");

    private final String displayValue;

    PaymentStatus(String displayValue) {
        this.displayValue = displayValue;
    }
}