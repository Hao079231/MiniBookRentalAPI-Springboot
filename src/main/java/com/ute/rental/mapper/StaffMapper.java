package com.ute.rental.mapper;

import com.ute.rental.dto.staff.ProfileStaffDto;
import com.ute.rental.dto.staff.StaffDto;
import com.ute.rental.form.staff.CreateStaffForm;
import com.ute.rental.form.staff.UpdateProfileForm;
import com.ute.rental.form.staff.UpdateStaffForm;
import com.ute.rental.model.Account;
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
    uses = {GroupMapper.class})
public interface StaffMapper {
  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "fullName", target = "fullName")
  @Mapping(source = "phone", target = "phone")
  @BeanMapping(ignoreByDefault = true)
  Account fromCreateStaffFormToEntity(CreateStaffForm createStaffForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "fullName", target = "fullName")
  @Mapping(source = "phone", target = "phone")
  @Mapping(source = "kind", target = "kind")
  @Mapping(source = "isAdmin", target = "isAdmin")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "group", target = "group", qualifiedByName = "fromEntityToGroupDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToStaffDto")
  StaffDto fromEntityToStaffDto(Account account);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "fullName", target = "fullName")
  @Mapping(source = "phone", target = "phone")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToProfileStaffDto")
  ProfileStaffDto fromEntityToProfileStaffDto(Account account);

  @IterableMapping(elementTargetType = StaffDto.class, qualifiedByName = "fromEntityToStaffDto")
  List<StaffDto> fromEntityToStaffDtoList(List<Account> accounts);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "fullName", target = "fullName")
  @Mapping(source = "phone", target = "phone")
  void FromUpdateStaffFormToEntity(UpdateStaffForm updateStaffForm, @MappingTarget Account account);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "fullName", target = "fullName")
  @Mapping(source = "phone", target = "phone")
  void FromUpdateProfileFormToEntity(UpdateProfileForm updateProfileForm, @MappingTarget Account account);
}
