package com.ute.rental.mapper;

import com.ute.rental.dto.reader.ReaderDto;
import com.ute.rental.dto.reader.ReaderDtoForStaff;
import com.ute.rental.form.reader.CreateReaderForm;
import com.ute.rental.form.reader.UpdateReaderForm;
import com.ute.rental.model.Reader;
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
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReaderMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "phone", target = "phone")
  @BeanMapping(ignoreByDefault = true)
  Reader fromCreateReaderFormToEntity(CreateReaderForm createReaderForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "phone", target = "phone")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToReaderDto")
  ReaderDto fromEntityToReaderDto(Reader reader);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "phone", target = "phone")
  @Mapping(source = "status", target = "status")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToReaderDtoForStaff")
  ReaderDtoForStaff fromEntityToReaderDtoForStaff(Reader reader);

  @IterableMapping(elementTargetType = ReaderDto.class, qualifiedByName = "fromEntityToReaderDto")
  List<ReaderDto> fromEntityToReaderDtoList(List<Reader> readers);

  @IterableMapping(elementTargetType = ReaderDtoForStaff.class, qualifiedByName = "fromEntityToReaderDtoForStaff")
  List<ReaderDtoForStaff> fromEntityToReaderDtoForStaffList(List<Reader> readers);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "phone", target = "phone")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateReaderFormToEntity(UpdateReaderForm updateReaderForm, @MappingTarget Reader reader);
}
