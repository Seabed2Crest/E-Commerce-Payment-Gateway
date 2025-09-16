package com.ecommerce.app.repository.tenantschema;

import com.ecommerce.app.entity.tenantschema.TenantCustomerPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantPaymentRepository extends JpaRepository<TenantCustomerPaymentEntity, String> {

    TenantCustomerPaymentEntity findByRazorPayOrderId(String razorPayOrderId);

    TenantCustomerPaymentEntity findByCustomerIdAndRazorPayOrderId(String customerId, String razorPayOrderId);
}
