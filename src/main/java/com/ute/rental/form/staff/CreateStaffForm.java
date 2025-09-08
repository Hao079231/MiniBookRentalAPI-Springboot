package com.ute.rental.form.staff;

import com.ute.rental.validation.Password;
import com.ute.rental.validation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema
public class CreateStaffForm {
  @NotEmpty(message = "username cannot be null")
  @Schema(name = "username", required = true)
  private String username;
  @Email
  @Schema(name = "email", required = true)
  private String email;
  @NotEmpty(message = "fullName cannot be null")
  @Schema(name = "fullName", required = true)
  private String fullName;
  @Password
  @Schema(name = "password", required = true)
  private String password;
  @Phone
  @Schema(name = "phone", required = true)
  private String phone;
}
