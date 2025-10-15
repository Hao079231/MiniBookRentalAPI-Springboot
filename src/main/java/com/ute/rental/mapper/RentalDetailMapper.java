package com.ute.rental.mapper;

import com.ute.rental.dto.rental.RentalDetailDto;
import com.ute.rental.model.RentalDetail;
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
    uses = {RentalTransactionMapper.class, BookMapper.class})
public interface RentalDetailMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "rentalTransaction", target = "rentalTransaction", qualifiedByName = "fromEntityToRentalDisplayDto")
  @Mapping(source = "book", target = "book", qualifiedByName = "fromEntityToBookDto")
  @Mapping(source = "refundAmount", target = "refundAmount")
  @Mapping(source = "bookCount", target = "bookCount")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToRentalDetailDto")
  RentalDetailDto fromEntityToRentalDetailDto(RentalDetail rentalDetail);

  @IterableMapping(elementTargetType = RentalDetailDto.class, qualifiedByName = "fromEntityToRentalDetailDto")
  List<RentalDetailDto> fromEntityToRentalDetailDtoList(List<RentalDetail> rentalDetails);
}
