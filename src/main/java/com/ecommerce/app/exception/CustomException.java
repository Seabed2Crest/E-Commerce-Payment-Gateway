package com.ecommerce.app.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class CustomException extends RuntimeException {

    private final int errorCode;

    private final String errorStatus;

    private final String errorMessage;

    protected CustomException(int errorCode, String errorStatus, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorStatus = errorStatus;
        this.errorMessage = errorMessage;
    }
}
