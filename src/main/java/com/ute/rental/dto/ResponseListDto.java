package com.ute.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseListDto<T>{
  private static final long serialVersionUID = 1L;
  private T content;
  private Long totalElement;
  private Integer totalPage;
}
