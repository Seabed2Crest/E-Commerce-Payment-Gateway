package com.ecommerce.app.enums;

public enum PaymentType {
    CARD,
    UPI,
    NET_BANKING,
    WALLET,
    PAY_LATER;

    public static PaymentType fromString(String method) {
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        return switch (method.trim().toLowerCase()) {
            case "card" -> CARD;
            case "upi" -> UPI;
            case "netbanking", "net_banking", "net-banking" -> NET_BANKING;
            case "wallet" -> WALLET;
            case "paylater", "pay_later", "pay-later" -> PAY_LATER;
            default -> throw new IllegalArgumentException("Unknown payment method: " + method);
        };
    }
}