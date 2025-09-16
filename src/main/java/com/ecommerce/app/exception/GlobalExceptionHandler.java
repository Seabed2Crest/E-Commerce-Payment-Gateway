package com.ecommerce.app.exception;

import com.ecommerce.app.response.CommonErrorResponse;
import com.ecommerce.app.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonErrorResponse> handleBaseCustomException(CustomException ex, HttpServletRequest request) {
        CommonErrorResponse response = new CommonErrorResponse();
        response.setErrorCode(ex.getErrorCode());
        response.setErrorStatus(ex.getErrorStatus());
        response.setErrorMessage(ex.getErrorMessage());
        response.setTimestamp(Instant.now().toString());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Invalid value"),
                        (existing, ignore) -> existing
                ));
        CommonErrorResponse response = new CommonErrorResponse();
        response.setErrorCode(HttpStatus.BAD_REQUEST.value());
        response.setErrorStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setErrorMessage("Validation failed");
        response.setTimestamp(ZonedDateTime.now(ZoneId.of(Constants.ZONE_ID)).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        response.setPath(request.getRequestURI());
        response.setFieldErrors(fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

}
