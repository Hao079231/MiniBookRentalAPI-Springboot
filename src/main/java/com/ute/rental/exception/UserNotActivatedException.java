package com.ute.rental.exception;

import lombok.Getter;

@Getter
public class UserNotActivatedException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private String code;

  public UserNotActivatedException(String message) {
    super(message);
  }

  public UserNotActivatedException(String message, String code) {
    super(message);
    this.code = code;
  }
}
