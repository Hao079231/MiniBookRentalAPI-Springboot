package com.ute.rental.form.reader;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class UnblockReaderForm {
  @NotNull(message = "id cannot be null")
  @Schema(name = "id")
  private Long id;
}
