package com.ecommerce.app.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonErrorResponse {

    private int errorCode;

    private String errorStatus;

    private String errorMessage;

    private String timestamp;

    private String path;

    private Map<String, String> fieldErrors;
}
