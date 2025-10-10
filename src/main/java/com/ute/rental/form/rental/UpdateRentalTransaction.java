package com.ute.rental.form.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class UpdateRentalTransaction {
  @NotNull(message = "id cannot be null")
  @Schema(name = "id")
  private Long id;
  @NotNull(message = "readerId cannot be null")
  @Schema(name = "readerId")
  private Long readerId;
}
