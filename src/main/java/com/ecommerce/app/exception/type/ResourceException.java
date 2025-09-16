package com.ecommerce.app.exception.type;

import com.ecommerce.app.exception.CustomException;

public class ResourceException extends CustomException {

  public ResourceException(int errorCode, String errorStatus, String errorMessage) {
    super(errorCode, errorStatus, errorMessage);
  }
}
