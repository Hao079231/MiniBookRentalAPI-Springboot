package com.ute.rental.mapper;

import com.ute.rental.dto.permission.PermissionDto;
import com.ute.rental.form.permission.CreatePermissionForm;
import com.ute.rental.model.Permission;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "action", target = "action")
  @Mapping(source = "nameGroup", target = "nameGroup")
  @Mapping(source = "permissionCode", target = "permissionCode")
  @BeanMapping(ignoreByDefault = true)
  Permission fromCreatePermissionFormToEntity(CreatePermissionForm createPermissionForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "action", target = "action")
  @Mapping(source = "nameGroup", target = "nameGroup")
  @Mapping(source = "permissionCode", target = "permissionCode")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToPermissionDto")
  PermissionDto fromEntityToPermissionDto(Permission permission);

  @IterableMapping(elementTargetType = PermissionDto.class, qualifiedByName = "fromEntityToPermissionDto")
  @Named("fromEntityToPermissionDtoList")
  List<PermissionDto> fromEntityToPermissionDtoList(List<Permission> permissions);
}
