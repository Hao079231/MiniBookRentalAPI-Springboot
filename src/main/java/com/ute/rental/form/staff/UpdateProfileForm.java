package com.ute.rental.form.staff;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema
public class UpdateProfileForm {
  @NotEmpty(message = "username cannot be null")
  @Schema(name = "username")
  private String username;
  @Email
  @Schema(name = "email")
  private String email;
  @Schema(name = "fullName")
  private String fullName;
  @NotEmpty(message = "phone cannot be null")
  @Schema(name = "phone")
  private String phone;
}
