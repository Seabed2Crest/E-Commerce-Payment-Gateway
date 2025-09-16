CREATE TABLE IF NOT EXISTS tenant_payment_entity (
    tenant_payment_id VARCHAR(255) PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL UNIQUE,
    customer_name VARCHAR(255) NOT NULL,
    customer_email_address VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255) NOT NULL UNIQUE,
    order_number_id VARCHAR(255) NOT NULL UNIQUE,
    total_amount DOUBLE PRECISION NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    payment_type VARCHAR(255) NOT NULL,
    payment_status VARCHAR(50) NOT NULL CHECK (
        payment_status IN ('PENDING', 'CREATED', 'FAILED', 'SUCCESS')
    ),
    razorpay_order_id VARCHAR(255) UNIQUE,
    razorpay_payment_id VARCHAR(255) UNIQUE,
    payment_initiated_at TIMESTAMP,
    payment_completed_at TIMESTAMP,
    payment_type_details JSONB
);
