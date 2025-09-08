package com.ute.rental.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
  private static final long serialVerionUID = 1L;
  private String code;

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, String code) {
    super(message);
    this.code = code;
  }
}
