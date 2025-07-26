package com.rizoshoeshop.rizoshoeshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Maps this exception to a 400 Bad Request HTTP status
public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }
}