package com.ute.rental.exception;

import lombok.Getter;

@Getter
public class UnauthorizationException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private String code;

  public UnauthorizationException(String message) {
    super(message);
  }

  public UnauthorizationException(String message, String code) {
    super(message);
    this.code = code;
  }
}
