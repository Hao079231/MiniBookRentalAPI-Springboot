package com.ute.rental.constant;

public class MiniBookConstant {
  public static final Integer KIND_ADMIN = 1;
  public static final Integer KIND_STAFF = 2;
  public static final Integer KIND_CUSTOMER = 3;

  public static final String EMAIL_PATTERN="^\\S+@\\S+\\.\\S+$";
  public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
  public static final String PHONE_PATTERN = "^0\\d{9}$";

}
