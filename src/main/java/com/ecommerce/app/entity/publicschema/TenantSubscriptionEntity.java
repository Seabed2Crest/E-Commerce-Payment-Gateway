package com.ecommerce.app.entity.publicschema;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tenant_subscription_data", schema = "public")
@Getter
@Setter
@ToString
public class TenantSubscriptionEntity {

    @Id
    @Column(name = "tenant_subscription_id", unique = true)
    private String tenantSubscriptionId;

    @Column(name = "tenant_id", unique = true, nullable = false)
    private String tenantId;

    @Column(name = "company_name", unique = true, nullable = false)
    private String companyName;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "schema_name", nullable = false)
    private String schemaName;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "business_type", nullable = false)
    private String businessType;

    @Column(name = "services_enabled", nullable = false)
    private List<String> servicesEnabled;

    @Column(name = "is_auto_payment_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isAutoPaymentEnabled = false;

    @Column(name = "is_subscription_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isSubscriptionActive = false;

    @OneToMany(mappedBy = "tenantSubscription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubscriptionPaymentEntity> subscriptionPayments = new ArrayList<>();
}
