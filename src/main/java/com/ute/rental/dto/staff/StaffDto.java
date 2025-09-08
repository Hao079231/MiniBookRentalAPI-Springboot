package com.ute.rental.dto.staff;

import com.ute.rental.dto.AbasicDto;
import com.ute.rental.dto.group.GroupDto;
import lombok.Data;

@Data
public class StaffDto extends AbasicDto {
  private Long id;
  private String username;
  private String email;
  private String fullName;
  private String phone;
  private Integer kind;
  private Boolean isAdmin;
  private GroupDto group;
}
