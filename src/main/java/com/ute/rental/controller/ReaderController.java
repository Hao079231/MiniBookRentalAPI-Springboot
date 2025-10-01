package com.ute.rental.controller;

import com.ute.rental.constant.MiniBookConstant;
import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.ErrorCode;
import com.ute.rental.dto.ResponseListDto;
import com.ute.rental.dto.reader.ReaderDto;
import com.ute.rental.dto.reader.ReaderDtoForStaff;
import com.ute.rental.exception.BadRequestException;
import com.ute.rental.exception.NotFoundException;
import com.ute.rental.form.reader.CreateReaderForm;
import com.ute.rental.form.reader.UnblockReaderForm;
import com.ute.rental.form.reader.UpdateReaderForm;
import com.ute.rental.mapper.ReaderMapper;
import com.ute.rental.model.Reader;
import com.ute.rental.model.criteria.ReaderCriteria;
import com.ute.rental.repository.ReaderRepository;
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
@RequestMapping("/v1/reader")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ReaderController extends ABasicController{
  @Autowired
  ReaderRepository readerRepository;

  @Autowired
  ReaderMapper readerMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('R_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateReaderForm request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (readerRepository.existsByEmail(request.getEmail())){
      throw new BadRequestException("Email already exist", ErrorCode.READER_ERROR_EXIST);
    }
    if (readerRepository.existsByPhone(request.getPhone())){
      throw new BadRequestException("Phone already exist", ErrorCode.READER_ERROR_EXIST);
    }
    Reader reader = readerMapper.fromCreateReaderFormToEntity(request);
    readerRepository.save(reader);
    apiMessageDto.setMessage("Create reader success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('R_L')")
  public ApiMessageDto<ResponseListDto<List<ReaderDto>>> getList(ReaderCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<ReaderDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ReaderDto>> responseListDto = new ResponseListDto<>();
    Page<Reader> readers = readerRepository.findAll(criteria.getSpecification(), pageable);
    responseListDto.setContent(readerMapper.fromEntityToReaderDtoList(readers.getContent()));
    responseListDto.setTotalElement(readers.getTotalElements());
    responseListDto.setTotalPage(readers.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list success");
    return apiMessageDto;
  }

  @GetMapping(value = "/staff-list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('R_SL')")
  public ApiMessageDto<ResponseListDto<List<ReaderDtoForStaff>>> getListForStaff(ReaderCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<ReaderDtoForStaff>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ReaderDtoForStaff>> responseListDto = new ResponseListDto<>();
    Page<Reader> readers = readerRepository.findAll(criteria.getSpecification(), pageable);
    responseListDto.setContent(readerMapper.fromEntityToReaderDtoForStaffList(readers.getContent()));
    responseListDto.setTotalElement(readers.getTotalElements());
    responseListDto.setTotalPage(readers.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('R_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateReaderForm request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Reader reader = readerRepository.findById(request.getId()).orElseThrow(()
    -> new NotFoundException("Reader not found", ErrorCode.READER_ERROR_NOT_FOUND));
    if (!Objects.equals(reader.getEmail(), request.getEmail())){
      if (readerRepository.existsByEmail(reader.getEmail())){
        throw new BadRequestException("Email already exist", ErrorCode.READER_ERROR_EXIST);
      }
    }

    if (!Objects.equals(reader.getPhone(), request.getPhone())){
      if (readerRepository.existsByPhone(reader.getPhone())){
        throw new BadRequestException("Phone already exist", ErrorCode.READER_ERROR_EXIST);
      }
    }

    if (Objects.equals(reader.getStatus(), MiniBookConstant.READER_STATUS_BLOCK)){
      throw new BadRequestException("Reader is blocked", ErrorCode.READER_ERROR_BLOCKED);
    }
    readerMapper.fromUpdateReaderFormToEntity(request, reader);
    readerRepository.save(reader);
    apiMessageDto.setMessage("Update success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('R_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new BadRequestException("Not allowed delete", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Reader reader = readerRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Reader not found", ErrorCode.READER_ERROR_NOT_FOUND));
    readerRepository.delete(reader);
    apiMessageDto.setMessage("Delete success");
    return apiMessageDto;
  }

  @PutMapping(value = "/unblock", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('R_UB')")
  public ApiMessageDto<String> unblock(@Valid @RequestBody UnblockReaderForm request, BindingResult bindingResult){
    if (!isAdmin()){
      throw new BadRequestException("Not allowed unblock", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Reader reader = readerRepository.findById(request.getId()).orElseThrow(()
    -> new NotFoundException("Reader not found", ErrorCode.READER_ERROR_NOT_FOUND));
    if (Objects.equals(reader.getStatus(), MiniBookConstant.READER_STATUS_ACTIVE)){
      throw new BadRequestException("Reader is active", ErrorCode.READER_ERROR_ACTIVED);
    }
    reader.setStatus(MiniBookConstant.READER_STATUS_ACTIVE);
    apiMessageDto.setMessage("Unblock success");
    return apiMessageDto;
  }
}
