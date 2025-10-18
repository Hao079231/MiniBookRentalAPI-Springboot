package com.ute.rental.controller;

import com.ute.rental.constant.MiniBookConstant;
import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.ErrorCode;
import com.ute.rental.dto.ResponseListDto;
import com.ute.rental.dto.staff.ProfileStaffDto;
import com.ute.rental.dto.staff.StaffDto;
import com.ute.rental.exception.BadRequestException;
import com.ute.rental.exception.NotFoundException;
import com.ute.rental.exception.UnauthorizationException;
import com.ute.rental.form.staff.CreateStaffForm;
import com.ute.rental.form.staff.UpdateProfileForm;
import com.ute.rental.form.staff.UpdateStaffForm;
import com.ute.rental.mapper.StaffMapper;
import com.ute.rental.model.Account;
import com.ute.rental.model.Group;
import com.ute.rental.model.criteria.StaffCriteria;
import com.ute.rental.repository.AccountRepository;
import com.ute.rental.repository.GroupRepository;
import com.ute.rental.repository.RentalTransactionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/v1/staff")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class StaffController extends ABasicController{
  @Autowired
  AccountRepository accountRepository;

  @Autowired
  StaffMapper staffMapper;

  @Autowired
  GroupRepository groupRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  RentalTransactionRepository rentalTransactionRepository;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('S_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateStaffForm value, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed create", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (accountRepository.existsByUsername(value.getUsername())){
      throw new BadRequestException("Username already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
    }
    if (accountRepository.existsByEmail(value.getEmail())){
      throw new BadRequestException("Email already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
    }
    if (accountRepository.existsByPhone(value.getPhone())){
      throw new BadRequestException("Phone already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
    }

    Group group = groupRepository.findByKind(MiniBookConstant.KIND_STAFF).orElseThrow(()
    -> new NotFoundException("Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND));

    Account account = staffMapper.fromCreateStaffFormToEntity(value);
    account.setPassword(passwordEncoder.encode(value.getPassword()));
    account.setKind(MiniBookConstant.KIND_STAFF);
    account.setIsAdmin(false);
    account.setGroup(group);
    accountRepository.save(account);
    apiMessageDto.setMessage("Create staff success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('S_L')")
  public ApiMessageDto<ResponseListDto<List<StaffDto>>> getList(StaffCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<StaffDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<StaffDto>> responseListDto = new ResponseListDto<>();
    Page<Account> accounts = accountRepository.findAll(criteria.getSpecification(), pageable);
    List<StaffDto> staffDtos = staffMapper.fromEntityToStaffDtoList(accounts.getContent());
    responseListDto.setContent(staffDtos);
    responseListDto.setTotalElement(accounts.getTotalElements());
    responseListDto.setTotalPage(accounts.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('S_V')")
  public ApiMessageDto<StaffDto> get(@PathVariable("id")  Long id){
    ApiMessageDto<StaffDto> apiMessageDto = new ApiMessageDto<>();
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed get", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    Account account = accountRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    apiMessageDto.setData(staffMapper.fromEntityToStaffDto(account));
    apiMessageDto.setMessage("Get success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('S_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateStaffForm request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed update", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    Account account = accountRepository.findById(request.getId()).orElseThrow(()
    -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    if (!Objects.equals(request.getUsername(), account.getUsername())){
      if (accountRepository.existsByUsername(request.getUsername())){
        throw new BadRequestException("Username already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }
    if (!Objects.equals(request.getEmail(), account.getEmail())){
      if (accountRepository.existsByEmail(request.getEmail())){
        throw new BadRequestException("Email already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }
    if (!Objects.equals(request.getPhone(), account.getPhone())){
      if (accountRepository.existsByPhone(request.getPhone())){
        throw new BadRequestException("Phone already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }
    staffMapper.FromUpdateStaffFormToEntity(request, account);
    if (StringUtils.isNotBlank(request.getNewPassword())){
      account.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }
    accountRepository.save(account);
    apiMessageDto.setMessage("Update staff success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('S_D')")
  @Transactional
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed delete", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Account account = accountRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    // Cập nhật staff_id = null cho các rental transaction liên quan
    rentalTransactionRepository.setStaffNullByStaffId(account.getId());
    accountRepository.delete(account);
    apiMessageDto.setMessage("Delete success");
    return apiMessageDto;
  }

  @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('S_P')")
  public ApiMessageDto<ProfileStaffDto> profile(){
    ApiMessageDto<ProfileStaffDto> apiMessageDto = new ApiMessageDto<>();
    Account account = accountRepository.findById(getCurrentUser()).orElseThrow(()
    -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    apiMessageDto.setData(staffMapper.fromEntityToProfileStaffDto(account));
    apiMessageDto.setMessage("Get profile success");
    return apiMessageDto;
  }

  @PutMapping(value = "/client-update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('S_SU')")
  public ApiMessageDto<String> updateProfile(@Valid @RequestBody UpdateProfileForm request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Account account = accountRepository.findById(getCurrentUser()).orElseThrow(()
    -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    if (!Objects.equals(request.getUsername(), account.getUsername())){
      if (accountRepository.existsByUsername(request.getUsername())){
        throw new BadRequestException("Username already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }
    if (!Objects.equals(request.getEmail(), account.getEmail())){
      if (accountRepository.existsByEmail(request.getEmail())){
        throw new BadRequestException("Email already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }
    if (!Objects.equals(request.getPhone(), account.getPhone())){
      if (accountRepository.existsByPhone(request.getPhone())){
        throw new BadRequestException("Phone already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }
    staffMapper.FromUpdateProfileFormToEntity(request, account);
    accountRepository.save(account);
    apiMessageDto.setMessage("Update profile success");
    return apiMessageDto;
  }
}
