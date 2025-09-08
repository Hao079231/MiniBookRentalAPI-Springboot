package com.ute.rental.dto.permission;

import lombok.Data;

@Data
public class PermissionDto {
  private Long id;
  private String name;
  private String description;
  private String action;
  private String nameGroup;
  private String permissionCode;
}
