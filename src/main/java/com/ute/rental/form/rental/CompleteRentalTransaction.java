package com.ute.rental.form.rental;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class CompleteRentalTransaction {
  @NotNull(message = "id cannot be null")
  @Schema(name = "id")
  private Long id;
}
