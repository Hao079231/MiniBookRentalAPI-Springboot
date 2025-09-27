package com.ute.rental.dto.book;

import com.ute.rental.dto.category.CategoryDto;
import lombok.Data;

@Data
public class BookDto {
  private Long id;
  private String title;
  private String author;
  private Float price;
  private Integer stock;
  private CategoryDto category;
}
