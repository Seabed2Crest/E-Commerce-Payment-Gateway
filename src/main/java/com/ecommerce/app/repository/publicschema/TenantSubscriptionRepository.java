package com.ecommerce.app.repository.publicschema;

import com.ecommerce.app.entity.publicschema.TenantSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantSubscriptionRepository extends JpaRepository<TenantSubscriptionEntity, String> {

    List<TenantSubscriptionEntity> findByIsSubscriptionActiveTrue();

    Optional<TenantSubscriptionEntity> findByTenantIdAndIsSubscriptionActiveTrue(String tenantId);

    Optional<TenantSubscriptionEntity> findByTenantId(String tenantId);

    @Query("""
                SELECT t
                FROM TenantSubscriptionEntity t
                JOIN FETCH t.subscriptionPayments p
                WHERE t.tenantId = :tenantId
                AND p.razorPayOrderId = :razorPayOrderId
            """)
    Optional<TenantSubscriptionEntity> findByTenantIdAndSubscriptionPaymentsRazorPayOrderId(
            String tenantId,
            String razorPayOrderId
    );

}
