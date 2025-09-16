package com.ecommerce.app.util;

public class Constants {

    private Constants() {

    }

    public static final String INVALID_PAYMENT_TYPE_ERR_MSG = "Invalid Payment Type";
    public static final String PAYMENT_MAPPING_ERR_MSG = "Error occurred while mapping the payment details.";
    public static final String RAZORPAY_CREATE_PAYMENT_ERR_MSG = "Error occurred while creating an order for the payment.";
    public static final String CREATE_PAYMENT_SUCCESS_MSG = "Payment Created Successfully.";
    public static final String PAYMENT_REGISTER_ERR_MSG = "Error occurred while creating payment register.";
    public static final String ACCOUNT_NOT_FOUND_ERR_MSG = "Payment Account Not Found.";
    public static final String PAYMENT_SUCCESS_MSG = "Payment Successful.";
    public static final String PAYMENT_FAILED_MSG = "Payment Failed.";
    public static final String PAYMENT_VERIFICATION_FAILED_ERR_MSG = "Payment verification failed. Please try again later.";
    public static final String ACCOUNT_CREATION_ERR_MSG = "Error occurred while creating an tenant account id";
    public static final String PAYMENT_VERIFICATION_ERR_MSG = "Payment record not found for verification.";
    public static final String RAZORPAY_CONTACT_TIMEOUT = "Request timed out while creating Razorpay contact.";
    public static final String RAZORPAY_CONTACT_IO_ERROR = "IO error occurred while creating Razorpay contact.";
    public static final String RAZORPAY_CONTACT_UNEXPECTED_ERROR = "An unexpected error occurred while creating Razorpay contact.";
    public static final String ACCOUNT_CREATED_SUCCESS_MSG = "Account created successfully.";

    public static final String ZONE_ID = "Asia/Kolkata";

    public static final String SUBSCRIPTION_PAYMENT_ID = "SP-ID";
    public static final String TENANT_SUBSCRIPTION_ID = "TS-ID";
    public static final String TENANT_PAYMENT_ID = "TP-ID";

}

