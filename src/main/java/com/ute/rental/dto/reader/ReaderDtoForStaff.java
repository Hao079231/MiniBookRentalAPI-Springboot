package com.ute.rental.dto.reader;

import com.ute.rental.dto.AbasicDto;
import lombok.Data;

@Data
public class ReaderDtoForStaff {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private Integer status;
}
