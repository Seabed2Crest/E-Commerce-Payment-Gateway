package com.ecommerce.app.util;

public class SecurityConstantsUrl {

    private SecurityConstantsUrl() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    protected static final String[] WHITE_LIST_URLS = {
            "/",
            "/favicon.ico",
            "/actuator/health",
            "/actuator/caches",
            "/actuator/info",
            "/actuator/prometheus",
            "/api/v1/payment/register",
            "/api/v1/payment/verifyRegister",
            "/api/v1/auth/token"
    };

    protected static final String[] TENANT_CUSTOMER_LIST_URLS = {
            "/api/v1/tenant/create-payment",
            "/api/v1/tenant/verify-payment",
    };

    public static String[] getWhiteListUrls() {
        return WHITE_LIST_URLS;
    }

    public static String[] getTenantCustomerListUrls() {
        return TENANT_CUSTOMER_LIST_URLS;
    }

}
