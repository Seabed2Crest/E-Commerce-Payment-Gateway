package com.ecommerce.app.schema;

public class TenantContextHolder {

    private TenantContextHolder() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    private static final ThreadLocal<String> context = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        context.set(tenantId);
    }

    public static String getTenantId() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
