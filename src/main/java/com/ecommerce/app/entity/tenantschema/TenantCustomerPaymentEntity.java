package com.ecommerce.app.entity.tenantschema;

import com.ecommerce.app.enums.PaymentStatus;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_payment_entity")
@Getter
@Setter
@ToString
public class TenantCustomerPaymentEntity {

    @Id
    @Column(name = "tenant_payment_id", nullable = false, unique = true)
    private String tenantPaymentId;

    @Column(name = "tenant_id", nullable = false, unique = true)
    private String tenantId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email_address", nullable = false, unique = true)
    private String customerEmailAddress;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "order_number_id", nullable = false, unique = true)
    private String orderNumberId;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "payment_type")
    private String paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "razorpay_order_id", unique = true)
    private String razorPayOrderId;

    @Column(name = "razorpay_payment_id", unique = true)
    private String razorPayPaymentId;

    @Column(name = "payment_initiated_at", updatable = false)
    private LocalDateTime paymentInitiatedAt;

    @Column(name = "payment_completed_at")
    private LocalDateTime paymentCompletedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_type_details", columnDefinition = "jsonb")
    private JsonNode paymentDetails;
}
