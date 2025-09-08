package com.ute.rental.controller;

import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.ErrorCode;
import com.ute.rental.dto.ResponseListDto;
import com.ute.rental.dto.group.GroupDto;
import com.ute.rental.exception.BadRequestException;
import com.ute.rental.exception.NotFoundException;
import com.ute.rental.exception.UnauthorizationException;
import com.ute.rental.form.group.CreateGroupForm;
import com.ute.rental.form.group.UpdateGroupForm;
import com.ute.rental.mapper.GroupMapper;
import com.ute.rental.model.Group;
import com.ute.rental.model.Permission;
import com.ute.rental.model.criteria.GroupCriteria;
import com.ute.rental.repository.GroupRepository;
import com.ute.rental.repository.PermissionRepository;
import jakarta.validation.Valid;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/group")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class GroupController extends ABasicController{
  @Autowired
  GroupRepository groupRepository;

  @Autowired
  GroupMapper groupMapper;

  @Autowired
  PermissionRepository permissionRepository;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('G_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateGroupForm value, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed create", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Group group = groupRepository.findFirstByName(value.getName()).orElse(null);
    if (group != null){
      throw new BadRequestException("Group name already exist", ErrorCode.GROUP_ERROR_EXIST);
    }
    group = groupMapper.fromCreateGroupFormToEntity(value);
    List<Permission> permissions = new ArrayList<>();
    for (int i = 0; i < value.getPermissions().length; i++){
      Permission permission = permissionRepository.findById(value.getPermissions()[i]).orElse(null);
      if (permission != null){
        permissions.add(permission);
      }
    }
    group.setPermissions(permissions);
    groupRepository.save(group);
    apiMessageDto.setMessage("Create success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('G_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateGroupForm value, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed update", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Group group = groupRepository.findById(value.getId()).orElseThrow(()
    -> new NotFoundException("Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND));
    if (!Objects.equals(group.getName(), value.getName())){
      if (groupRepository.existsByName(value.getName())){
        throw new BadRequestException("Group name already exist", ErrorCode.GROUP_ERROR_EXIST);
      }
    }
    groupMapper.fromUpdateGroupFormToEntity(value, group);
    List<Permission> permissions = new ArrayList<>();
    for (int i = 0; i < value.getPermissions().length; i++){
      Permission permission = permissionRepository.findById(value.getPermissions()[i]).orElse(null);
      if (permission != null){
        permissions.add(permission);
      }
    }
    group.setPermissions(permissions);
    groupRepository.save(group);
    apiMessageDto.setMessage("Update success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('G_V')")
  public ApiMessageDto<Group> get(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed get detail", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<Group> apiMessageDto = new ApiMessageDto<>();
    Group group = groupRepository.findById(id).orElse(null);
    apiMessageDto.setData(group);
    apiMessageDto.setMessage("Get success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('G_L')")
  public ApiMessageDto<ResponseListDto<List<GroupDto>>> list(GroupCriteria criteria, Pageable pageable){
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed get list", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<ResponseListDto<List<GroupDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<GroupDto>> responseListDto = new ResponseListDto<>();
    Page<Group> groups = groupRepository.findAll(criteria.getSpecification(), pageable);
    responseListDto.setContent(groupMapper.fromEntityToGroupDtoList(groups.getContent()));
    responseListDto.setTotalElement(groups.getTotalElements());
    responseListDto.setTotalPage(groups.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list success");
    return apiMessageDto;
  }
}
