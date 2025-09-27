package com.ute.rental.controller;

import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.ErrorCode;
import com.ute.rental.dto.ResponseListDto;
import com.ute.rental.dto.category.CategoryDto;
import com.ute.rental.exception.BadRequestException;
import com.ute.rental.exception.NotFoundException;
import com.ute.rental.form.category.CreateCategoryForm;
import com.ute.rental.form.category.UpdateCategoryForm;
import com.ute.rental.mapper.CategoryMapper;
import com.ute.rental.model.Category;
import com.ute.rental.repository.BookRepository;
import com.ute.rental.repository.CategoryRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CategoryController extends ABasicController{
  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  CategoryMapper categoryMapper;

  @Autowired
  BookRepository bookRepository;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_C')")
  public ApiMessageDto<String> create(@RequestBody @Valid CreateCategoryForm request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (!isAdmin()){
      throw new BadRequestException("Not allowed create", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    Category category = categoryRepository.findByName(request.getName()).orElse(null);
    if (category != null){
      throw new BadRequestException("Category name already exists", ErrorCode.CATEGORY_ERROR_EXIST);
    }
    category = categoryMapper.fromCreateCategoryFormToEntity(request);
    categoryRepository.save(category);
    apiMessageDto.setMessage("Create category success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_L')")
  public ApiMessageDto<ResponseListDto<List<CategoryDto>>> getList(Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CategoryDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CategoryDto>> responseListDto = new ResponseListDto<>();
    Page<Category> categories = categoryRepository.findAll(pageable);
    responseListDto.setContent(categoryMapper.fromEntityToCategoryDtoList(categories.getContent()));
    responseListDto.setTotalElement(categories.getTotalElements());
    responseListDto.setTotalPage(categories.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateCategoryForm request, BindingResult bindingResult){
    if (!isAdmin()){
      throw new BadRequestException("Not allowed update", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Category category = categoryRepository.findById(request.getId()).orElseThrow(() ->
        new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
    if (!Objects.equals(category.getName(), request.getName())){
      if (categoryRepository.existsByName(request.getName())){
        throw new BadRequestException("Category name already exist", ErrorCode.CATEGORY_ERROR_EXIST);
      }
    }
    categoryMapper.fromUpdateCategoryFormToEntity(request, category);
    categoryRepository.save(category);
    apiMessageDto.setMessage("Update category success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_D')")
  @Transactional
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new BadRequestException("Not allowed delete", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Category category = categoryRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
    categoryRepository.delete(category);
    bookRepository.removeCategoryId(id);
    apiMessageDto.setMessage("Delete category success");
    return apiMessageDto;
  }
}
