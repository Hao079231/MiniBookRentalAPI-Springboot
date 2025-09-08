package com.ute.rental.form.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class UpdateGroupForm {
  @NotNull(message = "id cannot be null")
  @Schema(name = "id", required = true)
  private Long id;
  @NotEmpty(message = "name cannot be null")
  @Schema(name = "name", required = true)
  private String name;
  @NotEmpty(message = "description cannot be null")
  @Schema(name = "description", required = true)
  private String description;
  @NotNull(message = "kind cannot be null")
  @Schema(name = "kind", required = true)
  private Integer kind;
  @NotNull(message = "permissions cannot be null")
  @Schema(name = "permissions", required = true)
  private Long[] permissions;
}
