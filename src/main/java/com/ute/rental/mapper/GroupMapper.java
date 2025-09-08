package com.ute.rental.mapper;

import com.ute.rental.dto.group.GroupDto;
import com.ute.rental.form.group.CreateGroupForm;
import com.ute.rental.form.group.UpdateGroupForm;
import com.ute.rental.model.Group;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {PermissionMapper.class})
public interface GroupMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "kind", target = "kind")
  @BeanMapping(ignoreByDefault = true)
  Group fromCreateGroupFormToEntity(CreateGroupForm createGroupForm);


  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "kind", target = "kind")
  @Mapping(source = "permissions", target = "permissions", qualifiedByName = "fromEntityToPermissionDtoList")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToGroupDto")
  GroupDto fromEntityToGroupDto(Group group);

  @IterableMapping(elementTargetType = GroupDto.class, qualifiedByName = "fromEntityToGroupDto")
  List<GroupDto> fromEntityToGroupDtoList(List<Group> groups);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "kind", target = "kind")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateGroupFormToEntity(UpdateGroupForm updateGroupForm, @MappingTarget Group group);
}
