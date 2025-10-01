package com.ute.rental.dto.reader;

import com.ute.rental.dto.AbasicDto;
import lombok.Data;

@Data
public class ReaderDto extends AbasicDto {
  private Long id;
  private String name;
  private String email;
  private String phone;
}
