package com.ute.rental.form.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class CreateRentalTransaction {
  @NotNull(message = "reader id cannot be null")
  @Schema(name = "readerId")
  private Long readerId;
}
