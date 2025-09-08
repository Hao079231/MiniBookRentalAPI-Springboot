package com.ute.rental.form.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema
public class CreatePermissionForm {
  @NotEmpty(message = "name cannot be null")
  @Schema(name = "name")
  private String name;
  @NotEmpty(message = "description cannot be null")
  @Schema(name = "description")
  private String description;
  @NotEmpty(message = "action cannot be null")
  @Schema(name = "action", example = "/v1/permission/create")
  private String action;
  @NotEmpty(message = "nameGroup cannot be null")
  @Schema(name = "nameGroup")
  private String nameGroup;
  @NotEmpty(message = "permissionCode cannot be null")
  @Schema(name = "permissionCode")
  private String permissionCode;
}
