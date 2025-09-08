package com.ute.rental.form;

import com.ute.rental.validation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema
public class AuthenticationForm {
  @Schema(name = "username", required = true)
  private String username;
  @Password
  @Schema(name = "password", required = true)
  private String password;
  @Email
  @Schema(name = "email", required = true)
  private String email;
  @NotEmpty(message = "grant type cannot be null")
  @Schema(name = "grantType", required = true)
  private String grantType;
}
