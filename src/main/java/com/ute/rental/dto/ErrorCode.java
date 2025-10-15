package com.ute.rental.dto;

public class ErrorCode {
  /**
   * Starting error code account
   * */
  public static final String ACCOUNT_ERROR_NOT_FOUND = "ERROR-ACCOUNT-0000";
  public static final String ACCOUNT_ERROR_EXIST = "ERROR-ACCOUNT-0001";
  public static final String ACCOUNT_ERROR_PASSWORD = "ERROR-ACCOUNT-0002";
  public static final String ACCOUNT_ERROR_UNAUTHORIZE = "ERROR-ACCOUNT-0003";
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

  /**
   * Starting error code category
   * */
  public static final String CATEGORY_ERROR_NOT_FOUND = "ERROR-CATEGORY-0000";
  public static final String CATEGORY_ERROR_EXIST = "ERROR-CATEGORY-0001";

  /**
   * Starting error code book
   * */
  public static final String BOOK_ERROR_NOT_FOUND = "ERROR-BOOK-0000";
  public static final String BOOK_ERROR_EXIST = "ERROR-BOOK-0001";
  public static final String BOOK_ERROR_CANNOT_DELETE = "ERROR-BOOK-0002";
  public static final String BOOK_ERROR_OUT_OF_STOCK = "ERROR-BOOK-0003";

  /**
   * Starting error code reader
   * */
  public static final String READER_ERROR_NOT_FOUND = "ERROR-READER-0000";
  public static final String READER_ERROR_EXIST = "ERROR-READER-0001";
  public static final String READER_ERROR_BLOCKED = "ERROR-READER-0002";
  public static final String READER_ERROR_ACTIVED = "ERROR-READER-0003";

  /**
   * Starting error code rental transaction
   * */
  public static final String RENTAL_TRANSACTION_ERROR_NOT_FOUND = "ERROR-RENTAL-TRANSACTION-0000";
  public static final String RENTAL_TRANSACTION_ERROR_EXIST = "ERROR-RENTAL-TRANSACTION-0001";
  public static final String RENTAL_TRANSACTION_ERROR_BORROW = "ERROR-RENTAL-TRANSACTION-0002";
  public static final String RENTAL_TRANSACTION_ERROR_STATE_NOT_RENTING = "ERROR-RENTAL-TRANSACTION-0003";

  /**
   * Starting error code rental detail
   * */
  public static final String RENTAL_DETAIL_ERROR_NOT_FOUND = "ERROR-RENTAL-DETAIL-0000";
  public static final String RENTAL_DETAIL_ERROR_EXIST = "ERROR-RENTAL-DETAIL-0001";
  public static final String RENTAL_DETAIL_ERROR_NOT_CREATE = "ERROR-RENTAL-DETAIL-0002";
  public static final String RENTAL_TRANSACTION_ERROR_BORROW_LIMIT = "ERROR-RENTAL-DETAIL-0003";
}
