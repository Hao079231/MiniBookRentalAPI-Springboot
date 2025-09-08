package com.ute.rental.controller;

import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.ErrorCode;
import com.ute.rental.dto.ResponseListDto;
import com.ute.rental.dto.permission.PermissionDto;
import com.ute.rental.exception.BadRequestException;
import com.ute.rental.exception.UnauthorizationException;
import com.ute.rental.form.permission.CreatePermissionForm;
import com.ute.rental.mapper.PermissionMapper;
import com.ute.rental.model.Permission;
import com.ute.rental.model.criteria.PermissionCriteria;
import com.ute.rental.repository.AccountRepository;
import com.ute.rental.repository.PermissionRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/permission")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class PermissionController extends ABasicController{
  @Autowired
  PermissionRepository permissionRepository;

  @Autowired
  PermissionMapper permissionMapper;

  @Autowired
  AccountRepository accountRepository;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('P_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreatePermissionForm value, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed create", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    if (permissionRepository.existsByAction(value.getAction())){
      throw new BadRequestException("Action already exist", ErrorCode.PERMISSION_ERROR_EXIST);
    }
    if (permissionRepository.existsByPermissionCode(value.getPermissionCode())){
      throw new BadRequestException("PCode already exist", ErrorCode.PERMISSION_ERROR_EXIST);
    }
    Permission permission = permissionMapper.fromCreatePermissionFormToEntity(value);
    permissionRepository.save(permission);
    apiMessageDto.setMessage("Create permission success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('P_L')")
  public ApiMessageDto<ResponseListDto<List<PermissionDto>>> getList(PermissionCriteria criteria, Pageable pageable){
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed get list", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<ResponseListDto<List<PermissionDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<PermissionDto>> responseListDto = new ResponseListDto<>();
    Page<Permission> permissions = permissionRepository.findAll(criteria.getSpecification(), pageable);
    responseListDto.setContent(permissionMapper.fromEntityToPermissionDtoList(permissions.getContent()));
    responseListDto.setTotalElement(permissions.getTotalElements());
    responseListDto.setTotalPage(permissions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list success");
    return apiMessageDto;
  }
}
