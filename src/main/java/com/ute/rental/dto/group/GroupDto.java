package com.ute.rental.dto.group;

import com.ute.rental.dto.AbasicDto;
import com.ute.rental.dto.permission.PermissionDto;
import java.util.List;
import lombok.Data;

@Data
public class GroupDto extends AbasicDto {
  private Long id;
  private String name;
  private String description;
  private int kind;
  private List<PermissionDto> permissions;
}
