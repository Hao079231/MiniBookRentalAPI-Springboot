package com.ute.rental.dto;

public class ErrorCode {
  /**
   * Starting error code account
   * */
  public static final String ACCOUNT_ERROR_NOT_FOUND = "ACCOUNT-ERROR-0000";
  public static final String ACCOUNT_ERROR_EXIST = "ACCOUNT-ERROR-0001";
  public static final String ACCOUNT_ERROR_PASSWORD = "ACCOUNT-ERROR-0002";
  public static final String ACCOUNT_ERROR_UNAUTHORIZE = "ACCOUNT-ERROR-0003";
  /**
   * Starting error code database
   * */
  public static final String ERROR_DB_QUERY = "ERROR-DB-QUERY-0000";

  /**
   * Starting error code permission
   * */
  public static final String PERMISSION_ERROR_NOT_FOUND = "ERROR-PERMISSION-0000";
  public static final String PERMISSION_ERROR_EXIST = "ERROR-PERMISSION-0001";

  /**
   * Starting error code permission
   * */
  public static final String GROUP_ERROR_NOT_FOUND = "ERROR-GROUP-0000";
  public static final String GROUP_ERROR_EXIST = "ERROR-GROUP-0001";

  /**
   * Starting error code token
   * */
  public static final String TOKEN_ERROR_INVALID = "ERROR-TOKEN-0000";
}
