package com.ute.rental.form.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema
public class CreateCategoryForm {
  @NotEmpty(message = "name cannot be null")
  @Schema(name = "name")
  private String name;
}
