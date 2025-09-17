package com.ecommerce.app.request.register;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPaymentRequest {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "User name is required")
    private String userName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Only Gmail addresses are allowed")
    private String emailAddress;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Business type is mandatory")
    private String businessType;

    @NotNull(message = "Service enabled options is required")
    @Size(min = 1, message = "At least one service must be enabled")
    private List<String> servicesEnabled;

    @NotNull(message = "Auto payment flag is required")
    private Boolean isAutoPaymentEnabled;

    @NotNull(message = "Service start date is required")
    @FutureOrPresent(message = "Service start date must be today or in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate serviceStartDate;

    @NotNull(message = "Service end date is required")
    @Future(message = "Service end date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate serviceEndDate;

    @NotBlank(message = "Service period is required")
    @Pattern(regexp = "MONTHLY|THREE_MONTHS|SIX_MONTHS|YEAR", message = "Service period must be MONTHLY or THREE_MONTHS or SIX_MONTHS or YEAR")
    private String servicePeriod;

    @NotNull(message = "Subscription amount is required")
    @Positive(message = "Subscription amount must be greater than zero")
    private Double subscriptionAmount;
}
