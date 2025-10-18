package com.ute.rental.controller;

import com.ute.rental.constant.MiniBookConstant;
import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.ErrorCode;
import com.ute.rental.dto.ResponseListDto;
import com.ute.rental.dto.rental.RentalTransactionDisplayDto;
import com.ute.rental.dto.rental.RentalTransactionDto;
import com.ute.rental.dto.rental.RentalTransactionForStaffDto;
import com.ute.rental.exception.BadRequestException;
import com.ute.rental.exception.NotFoundException;
import com.ute.rental.exception.UnauthorizationException;
import com.ute.rental.form.rental.CompleteRentalTransaction;
import com.ute.rental.form.rental.CreateRentalTransaction;
import com.ute.rental.form.rental.UpdateRentalTransaction;
import com.ute.rental.mapper.RentalTransactionMapper;
import com.ute.rental.model.Account;
import com.ute.rental.model.Reader;
import com.ute.rental.model.RentalDetail;
import com.ute.rental.model.RentalTransaction;
import com.ute.rental.model.criteria.RentalTransactionCriteria;
import com.ute.rental.repository.AccountRepository;
import com.ute.rental.repository.BookRepository;
import com.ute.rental.repository.ReaderRepository;
import com.ute.rental.repository.RentalDetailRepository;
import com.ute.rental.repository.RentalTransactionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/rental-transaction")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class RentalTransactionController extends ABasicController{
  @Autowired
  RentalTransactionRepository rentalTransactionRepository;

  @Autowired
  RentalTransactionMapper rentalTransactionMapper;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  ReaderRepository readerRepository;

  @Autowired
  RentalDetailRepository rentalDetailRepository;

  @Autowired
  BookRepository bookRepository;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RT_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateRentalTransaction request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Account staff = accountRepository.findById(getCurrentUser()).orElseThrow(()
    -> new NotFoundException("Staff not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    Reader reader = readerRepository.findById(request.getReaderId()).orElseThrow(()
    -> new NotFoundException("Reader not found", ErrorCode.READER_ERROR_NOT_FOUND));
    if (!Objects.equals(reader.getStatus(), MiniBookConstant.READER_STATUS_ACTIVE)){
      throw new BadRequestException("Reader has been blocked", ErrorCode.READER_ERROR_BLOCKED);
    }
    if (rentalTransactionRepository.existsByReaderIdAndState(reader.getId(), MiniBookConstant.RENTAL_STATE_RENTING)
    || rentalTransactionRepository.existsByReaderIdAndState(reader.getId(), MiniBookConstant.RENTAL_STATE_OVERDUE)){
      throw new BadRequestException("Reader has already borrowed the book", ErrorCode.RENTAL_TRANSACTION_ERROR_EXIST);
    }
    RentalTransaction rentalTransaction = new RentalTransaction();
    rentalTransaction.setState(MiniBookConstant.RENTAL_STATE_RENTING);
    rentalTransaction.setReader(reader);
    rentalTransaction.setStaff(staff);
    rentalTransactionRepository.save(rentalTransaction);
    apiMessageDto.setMessage("Create rental transaction success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RT_L')")
  public ApiMessageDto<ResponseListDto<List<RentalTransactionDisplayDto>>> getList(
      RentalTransactionCriteria criteria, Pageable pageable) {

    ApiMessageDto<ResponseListDto<List<RentalTransactionDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<RentalTransactionDisplayDto>> responseListDto = new ResponseListDto<>();

    Page<RentalTransaction> rentalTransactions =
        rentalTransactionRepository.findAll(criteria.getSpecification(), pageable);

    List<RentalTransactionDisplayDto> dtoList =
        rentalTransactionMapper.fromEntityToRentalDisplayDtoList(rentalTransactions.getContent());

    // Tính số ngày mượn
    for (int i = 0; i < dtoList.size(); i++) {
      RentalTransaction entity = rentalTransactions.getContent().get(i);
      RentalTransactionDisplayDto dto = dtoList.get(i);

      if (entity.getDueDate() != null && entity.getCreatedDate() != null) {
        long diffInMillies = entity.getDueDate().getTime() - entity.getCreatedDate().getTime();
        long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
        dto.setDaysBorrowed(diffInDays);
      } else {
        dto.setDaysBorrowed(null);
      }
    }

    responseListDto.setContent(dtoList);
    responseListDto.setTotalElement(rentalTransactions.getTotalElements());
    responseListDto.setTotalPage(rentalTransactions.getTotalPages());

    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list rental transaction success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RT_V')")
  public ApiMessageDto<RentalTransactionDto> get(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("Not allowed get", ErrorCode.ACCOUNT_ERROR_UNAUTHORIZE);
    }
    ApiMessageDto<RentalTransactionDto> apiMessageDto = new ApiMessageDto<>();
    RentalTransaction rentalTransaction = rentalTransactionRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Rental transaction not found", ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));
    apiMessageDto.setData(rentalTransactionMapper.fromEntityToRentalTransactionDto(rentalTransaction));
    apiMessageDto.setMessage("Get detail rental transaction success");
    return apiMessageDto;
  }

  @GetMapping(value = "/staff-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RT_STV')")
  public ApiMessageDto<RentalTransactionForStaffDto> getForStaff(@PathVariable("id") Long id){
    ApiMessageDto<RentalTransactionForStaffDto> apiMessageDto = new ApiMessageDto<>();
    RentalTransaction rentalTransaction = rentalTransactionRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Rental transaction not found", ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));
    apiMessageDto.setData(rentalTransactionMapper.fromEntityToRentalTransactionForStaffDto(rentalTransaction));
    apiMessageDto.setMessage("Get detail rental transaction success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RT_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateRentalTransaction request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    RentalTransaction rentalTransaction = rentalTransactionRepository.findById(request.getId()).orElseThrow(()
    -> new NotFoundException("Rental transaction not found", ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));
    if (!Objects.equals(rentalTransaction.getStaff().getId(), getCurrentUser())){
      Account staff = accountRepository.findById(getCurrentUser()).orElseThrow(()
          -> new NotFoundException("Staff not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
      rentalTransaction.setStaff(staff);
    }
    if (!Objects.equals(rentalTransaction.getStaff().getId(), request.getReaderId())){
      Reader reader = readerRepository.findById(request.getReaderId()).orElseThrow(()
          -> new NotFoundException("Reader not found", ErrorCode.READER_ERROR_NOT_FOUND));
      if (rentalTransactionRepository.existsByReaderIdAndState(reader.getId(), MiniBookConstant.RENTAL_STATE_RENTING)
      || rentalTransactionRepository.existsByReaderIdAndState(reader.getId(), MiniBookConstant.RENTAL_STATE_OVERDUE)){
        throw new BadRequestException("Reader has already borrowed the book", ErrorCode.RENTAL_TRANSACTION_ERROR_EXIST);
      }
      rentalTransaction.setReader(reader);
    }
    rentalTransactionRepository.save(rentalTransaction);
    apiMessageDto.setMessage("Update rental transaction success");
    return apiMessageDto;
  }

  @PutMapping(value = "/complete", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RT_CR')")
  @Transactional
  public ApiMessageDto<String> completeBookReturn(@Valid @RequestBody CompleteRentalTransaction request,
      BindingResult bindingResult) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    RentalTransaction rentalTransaction = rentalTransactionRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Rental transaction not found",
            ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));

    // Lấy tất cả rental detail kèm book trong 1 truy vấn duy nhất (JOIN FETCH)
    List<RentalDetail> rentalDetails = rentalDetailRepository.findByRentalTransactionIdWithBook(rentalTransaction.getId());

    if (rentalDetails.isEmpty()) {
      throw new NotFoundException("No rental details found for this transaction",
          ErrorCode.RENTAL_DETAIL_ERROR_NOT_FOUND);
    }

    // Gom nhóm số lượng mượn của từng bookId (tránh cập nhật trùng book)
    Map<Long, Integer> stockChanges = new HashMap<>();
    for (RentalDetail detail : rentalDetails) {
      Long bookId = detail.getBook().getId();
      stockChanges.merge(bookId, detail.getBookCount(), Integer::sum);
    }

    // Cập nhật tồn kho hàng loạt (bulk update)
    stockChanges.forEach((bookId, count) -> {
      bookRepository.updateStock(bookId, count);
    });

    rentalTransaction.setState(MiniBookConstant.RENTAL_STATE_COMPLETE);
    rentalTransaction.setDueDate(new Date());
    rentalTransactionRepository.save(rentalTransaction);

    apiMessageDto.setMessage("Complete book rental success");
    return apiMessageDto;
  }

}
