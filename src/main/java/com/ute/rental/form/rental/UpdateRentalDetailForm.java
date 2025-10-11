package com.ute.rental.form.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class UpdateRentalDetailForm {
  @NotNull(message = "id cannot be null")
  @Schema(name = "id")
  private Long id;
  @NotNull(message = "rentalTransactionId cannot be null")
  @Schema(name = "rentalTransactionId")
  private Long rentalTransactionId;
  @NotNull(message = "bookId cannot be null")
  @Schema(name = "bookId")
  private Long bookId;
}
