package com.ute.rental.form.reader;

import com.ute.rental.validation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class UpdateReaderForm {
  @NotNull(message = "id cannot be null")
  @Schema(name = "id")
  private Long id;
  @Schema(name = "name")
  private String name;
  @Email
  @Schema(name = "email")
  private String email;
  @Phone
  @Schema(name = "phone")
  private String phone;
}
