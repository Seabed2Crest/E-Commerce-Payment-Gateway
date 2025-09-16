package com.ecommerce.app.entity.publicschema;

import com.ecommerce.app.enums.PaymentType;
import com.ecommerce.app.enums.ServiceType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_payment", schema = "public")
@Getter
@Setter
@ToString
public class SubscriptionPaymentEntity {

    @Id
    @Column(name = "subscription_payment_id", unique = true, nullable = false)
    private String subscriptionPaymentId;

    @Column(name = "razorpay_order_id", nullable = false, unique = true)
    private String razorPayOrderId;

    @Column(name = "subscription_amount", nullable = false)
    private Double subscriptionAmount;

    @Column(name = "razorpay_payment_id", unique = true)
    private String razorPayPaymentId;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "service_start_date", nullable = false)
    private LocalDate serviceStartDate;

    @Column(name = "service_end_date", nullable = false)
    private LocalDate serviceEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_period", nullable = false)
    private ServiceType servicePeriod;

    @Column(name = "payment_initiated_at", nullable = false, updatable = false)
    private LocalDateTime paymentInitiatedAt;

    @Column(name = "payment_completed_at")
    private LocalDateTime paymentCompletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_details", columnDefinition = "jsonb")
    private JsonNode paymentDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_subscription_id", nullable = false)
    private TenantSubscriptionEntity tenantSubscription;
}
