package com.ute.rental.mapper;

import com.ute.rental.dto.rental.RentalTransactionDisplayDto;
import com.ute.rental.dto.rental.RentalTransactionDto;
import com.ute.rental.dto.rental.RentalTransactionForStaffDto;
import com.ute.rental.model.RentalTransaction;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {StaffMapper.class, ReaderMapper.class})
public interface RentalTransactionMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "staff", target = "staff", qualifiedByName = "fromEntityToProfileStaffDto")
  @Mapping(source = "reader", target = "reader", qualifiedByName = "fromEntityToReaderDtoForStaff")
  @Mapping(source = "dueDate", target = "dueDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToRentalDisplayDto")
  RentalTransactionDisplayDto fromEntityToRentalDisplayDto(RentalTransaction rentalTransaction);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "staff", target = "staff", qualifiedByName = "fromEntityToStaffDto")
  @Mapping(source = "reader", target = "reader", qualifiedByName = "fromEntityToReaderDto")
  @Mapping(source = "depositTotal", target = "depositTotal")
  @Mapping(source = "refundAmountTotal", target = "refundAmountTotal")
  @Mapping(source = "dueDate", target = "dueDate")
  @Mapping(source = "state", target = "state")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToRentalTransactionDto")
  RentalTransactionDto fromEntityToRentalTransactionDto(RentalTransaction rentalTransaction);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "staff", target = "staff", qualifiedByName = "fromEntityToProfileStaffDto")
  @Mapping(source = "reader", target = "reader", qualifiedByName = "fromEntityToReaderDtoForStaff")
  @Mapping(source = "depositTotal", target = "depositTotal")
  @Mapping(source = "refundAmountTotal", target = "refundAmountTotal")
  @Mapping(source = "dueDate", target = "dueDate")
  @Mapping(source = "state", target = "state")
  @Mapping(source = "createdDate", target = "createdDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToRentalTransactionDto")
  RentalTransactionForStaffDto fromEntityToRentalTransactionForStaffDto(RentalTransaction rentalTransaction);

  @IterableMapping(elementTargetType = RentalTransactionDisplayDto.class, qualifiedByName = "fromEntityToRentalDisplayDto")
  List<RentalTransactionDisplayDto> fromEntityToRentalDisplayDtoList(List<RentalTransaction> rentalTransactions);
}
