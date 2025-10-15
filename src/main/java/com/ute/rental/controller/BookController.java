package com.ute.rental.controller;

import com.ute.rental.constant.MiniBookConstant;
import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.ErrorCode;
import com.ute.rental.dto.ResponseListDto;
import com.ute.rental.dto.book.BookDto;
import com.ute.rental.exception.BadRequestException;
import com.ute.rental.exception.NotFoundException;
import com.ute.rental.form.book.CreateBookForm;
import com.ute.rental.form.book.UpdateBookForm;
import com.ute.rental.mapper.BookMapper;
import com.ute.rental.model.Book;
import com.ute.rental.model.Category;
import com.ute.rental.model.RentalDetail;
import com.ute.rental.model.criteria.BookCriteria;
import com.ute.rental.repository.BookRepository;
import com.ute.rental.repository.CategoryRepository;
import com.ute.rental.repository.RentalDetailRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/book")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class BookController {
  @Autowired
  BookRepository bookRepository;

  @Autowired
  BookMapper bookMapper;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  RentalDetailRepository rentalDetailRepository;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BO_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateBookForm request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Book book = bookRepository.findByTitleAndAuthor(request.getTitle(), request.getAuthor()).orElse(null);
    if (book != null){
      throw new BadRequestException("Book already exist", ErrorCode.BOOK_ERROR_EXIST);
    }
    Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(()
    -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
    book = bookMapper.fromCreateBookFormToEntity(request);
    book.setCategory(category);
    bookRepository.save(book);
    apiMessageDto.setMessage("Create book success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BO_L')")
  public ApiMessageDto<ResponseListDto<List<BookDto>>> getList(BookCriteria bookCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<BookDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<BookDto>> responseListDto = new ResponseListDto<>();
    Page<Book> books = bookRepository.findAll(bookCriteria.getSpecification(), pageable);
    responseListDto.setContent(bookMapper.fromEntityToBookDtoList(books.getContent()));
    responseListDto.setTotalElement(books.getTotalElements());
    responseListDto.setTotalPage(books.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BO_V')")
  public ApiMessageDto<BookDto> get(@PathVariable("id") Long id){
    ApiMessageDto<BookDto> apiMessageDto = new ApiMessageDto<>();
    Book book = bookRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Book not found", ErrorCode.BOOK_ERROR_NOT_FOUND));
    apiMessageDto.setData(bookMapper.fromEntityToBookDto(book));
    apiMessageDto.setMessage("Get book success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BO_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateBookForm request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Book book = bookRepository.findById(request.getId()).orElseThrow(()
    -> new NotFoundException("Book not found", ErrorCode.BOOK_ERROR_NOT_FOUND));
    if (!Objects.equals(request.getTitle(), book.getTitle()) || !Objects.equals(request.getAuthor(), book.getAuthor())){
      if(bookRepository.existsByTitleAndAuthor(request.getTitle(), request.getAuthor())){
        throw new BadRequestException("Book already exist", ErrorCode.BOOK_ERROR_EXIST);
      }
    }
    Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(()
    -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
    bookMapper.fromUpdateBookFormToEntity(request, book);
    book.setCategory(category);
    bookRepository.save(book);
    apiMessageDto.setMessage("Update book success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BO_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Book not found", ErrorCode.BOOK_ERROR_NOT_FOUND));

    // Lấy toàn bộ rental detail chứa sách này
    List<RentalDetail> rentalDetails = rentalDetailRepository.findByBookId(id);

    // Kiểm tra nếu có rental transaction đang thuê (state = 1)
    boolean hasActiveRent = rentalDetails.stream()
        .anyMatch(rd -> rd.getRentalTransaction() != null && rd.getRentalTransaction().getState() == 1);

    if (hasActiveRent) {
      throw new BadRequestException("Cannot delete book with state renting", ErrorCode.BOOK_ERROR_CANNOT_DELETE);
    }

    // Nếu không có state renting, set book = null trong tất cả rental detail
    for (RentalDetail rd : rentalDetails) {
      rd.setBook(null);
    }
    rentalDetailRepository.saveAll(rentalDetails);
    bookRepository.delete(book);
    apiMessageDto.setMessage("Delete book success");
    return apiMessageDto;
  }
}
