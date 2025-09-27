package com.ute.rental.mapper;

import com.ute.rental.dto.book.BookDto;
import com.ute.rental.form.book.CreateBookForm;
import com.ute.rental.form.book.UpdateBookForm;
import com.ute.rental.model.Book;
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
    uses = {CategoryMapper.class})
public interface BookMapper {
  @Mapping(source = "title", target = "title")
  @Mapping(source = "author", target = "author")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "stock", target = "stock")
  @BeanMapping(ignoreByDefault = true)
  Book fromCreateBookFormToEntity(CreateBookForm createBookForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "author", target = "author")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "stock", target = "stock")
  @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToBookDto")
  BookDto fromEntityToBookDto(Book book);

  @IterableMapping(elementTargetType = BookDto.class, qualifiedByName = "fromEntityToBookDto")
  List<BookDto> fromEntityToBookDtoList(List<Book> books);

  @Mapping(source = "title", target = "title")
  @Mapping(source = "author", target = "author")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "stock", target = "stock")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateBookFormToEntity(UpdateBookForm updateBookForm, @MappingTarget Book book);
}
