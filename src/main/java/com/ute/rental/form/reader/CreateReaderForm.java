package com.ute.rental.form.reader;

import com.ute.rental.validation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Schema
public class CreateReaderForm {
  @NotEmpty(message = "name cannot be null")
  @Schema(name = "name")
  private String name;
  @Email
  @Schema(name = "email")
  private String email;
  @Phone
  @Schema(name = "phone")
  private String phone;
}
