package com.ute.rental.form.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class CreateBookForm {
  @NotEmpty(message = "Title cannot be null")
  @Schema(name = "title")
  private String title;
  @NotEmpty(message = "Author cannot be null")
  @Schema(name = "author")
  private String author;
  @NotNull(message = "Price cannot be null")
  @Schema(name = "price")
  private Float price;
  @NotNull(message = "Stock cannot be null")
  @Schema(name = "stock")
  private Integer stock;
  @NotNull(message = "CategoryId cannot be null")
  @Schema(name = "categoryId")
  private Long categoryId;
}
