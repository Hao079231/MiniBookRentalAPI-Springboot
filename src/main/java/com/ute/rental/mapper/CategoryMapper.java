package com.ute.rental.mapper;

import com.ute.rental.dto.category.CategoryDto;
import com.ute.rental.form.category.CreateCategoryForm;
import com.ute.rental.form.category.UpdateCategoryForm;
import com.ute.rental.model.Category;
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
public interface CategoryMapper {
  @Mapping(source = "name", target = "name")
  @BeanMapping(ignoreByDefault = true)
  Category fromCreateCategoryFormToEntity(CreateCategoryForm createCategoryForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCategoryDto")
  CategoryDto fromEntityToCategoryDto(Category category);

  @IterableMapping(elementTargetType = CategoryDto.class, qualifiedByName = "fromEntityToCategoryDto")
  List<CategoryDto> fromEntityToCategoryDtoList(List<Category> categories);

  @Mapping(source = "name", target = "name")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateCategoryFormToEntity(UpdateCategoryForm updateCategoryForm, @MappingTarget Category category);
}
