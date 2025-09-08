package com.ute.rental.form.staff;

import com.ute.rental.validation.Password;
import com.ute.rental.validation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class UpdateStaffForm {
  @NotNull(message = "id cannot be null")
  @Schema(name = "id")
  private Long id;
  @NotEmpty(message = "username cannot be null")
  @Schema(name = "username")
  private String username;
  @Email
  @Schema(name = "email")
  private String email;
  @Password
  @Schema(name = "newPassword")
  private String newPassword;
  @Schema(name = "fullName")
  private String fullName;
  @Phone
  @Schema(name = "phone")
  private String phone;
}
