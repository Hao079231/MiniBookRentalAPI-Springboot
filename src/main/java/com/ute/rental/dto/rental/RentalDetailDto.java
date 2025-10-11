package com.ute.rental.dto.rental;

import com.ute.rental.dto.book.BookDto;
import lombok.Data;

@Data
public class RentalDetailDto {
  private Long id;
  private RentalTransactionDisplayDto rentalTransaction;
  private BookDto book;
  private Float refundAmount;
}
