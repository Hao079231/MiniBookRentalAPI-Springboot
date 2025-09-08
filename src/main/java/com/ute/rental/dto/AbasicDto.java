package com.ute.rental.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AbasicDto {
  private int status;
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;
}
