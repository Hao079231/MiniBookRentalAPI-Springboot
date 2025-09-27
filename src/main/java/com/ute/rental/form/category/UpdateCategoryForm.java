package com.ute.rental.form.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class UpdateCategoryForm {
  @NotNull(message = "id cannot be null")
  @Schema(name = "id")
  private Long id;
  @NotEmpty(message = "name cannot be null")
  @Schema(name = "name")
  private String name;
}
