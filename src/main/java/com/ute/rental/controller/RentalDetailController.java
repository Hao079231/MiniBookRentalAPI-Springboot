package com.ute.rental.controller;

import com.ute.rental.constant.MiniBookConstant;
import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.ErrorCode;
import com.ute.rental.dto.ResponseListDto;
import com.ute.rental.dto.rental.RentalDetailDto;
import com.ute.rental.exception.BadRequestException;
import com.ute.rental.exception.NotFoundException;
import com.ute.rental.form.rental.CreateRentalDetailForm;
import com.ute.rental.form.rental.UpdateRentalDetailForm;
import com.ute.rental.mapper.RentalDetailMapper;
import com.ute.rental.model.Book;
import com.ute.rental.model.RentalDetail;
import com.ute.rental.model.RentalTransaction;
import com.ute.rental.model.criteria.RentalDetailCriteria;
import com.ute.rental.repository.BookRepository;
import com.ute.rental.repository.RentalDetailRepository;
import com.ute.rental.repository.RentalTransactionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/v1/rental-detail")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class RentalDetailController extends ABasicController{
  @Autowired
  RentalDetailRepository rentalDetailRepository;

  @Autowired
  RentalDetailMapper rentalDetailMapper;

  @Autowired
  RentalTransactionRepository rentalTransactionRepository;

  @Autowired
  BookRepository bookRepository;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RD_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateRentalDetailForm request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (rentalDetailRepository.existsByBookIdAndRentalTransactionId(request.getBookId(), request.getRentalTransactionId())){
      throw new BadRequestException("Rental detail already exist", ErrorCode.RENTAL_DETAIL_ERROR_EXIST);
    }
    Book book = bookRepository.findById(request.getBookId()).orElseThrow(()
    -> new NotFoundException("Book not found", ErrorCode.BOOK_ERROR_NOT_FOUND));
    if (book.getStock() == 0 || book.getStock() < request.getBookCount()){
      throw new BadRequestException("Book stock is not enough", ErrorCode.BOOK_ERROR_OUT_OF_STOCK);
    }
    RentalTransaction rentalTransaction = rentalTransactionRepository.findById(request.getRentalTransactionId()).orElseThrow(()
    -> new NotFoundException("Rental transaction not found", ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));
    if (!Objects.equals(getCurrentUser(), rentalTransaction.getStaff().getId())){
      throw new BadRequestException("Cannot create rental detail with this staff", ErrorCode.RENTAL_DETAIL_ERROR_NOT_CREATE);
    }
    if (rentalTransaction.getTotalBorrowed() + request.getBookCount() > 3){
      throw new BadRequestException("Borrowed 3 books", ErrorCode.RENTAL_TRANSACTION_ERROR_BORROW);
    }
    if (!Objects.equals(rentalTransaction.getState(), MiniBookConstant.RENTAL_STATE_RENTING)){
      throw new BadRequestException("Cannot add if the state of the rental transaction is not renting", ErrorCode.RENTAL_TRANSACTION_ERROR_STATE_NOT_RENTING);
    }
    RentalDetail rentalDetail = new RentalDetail();
    rentalDetail.setBook(book);
    rentalDetail.setBookCount(request.getBookCount());
    rentalDetail.setRentalTransaction(rentalTransaction);
    rentalDetail.setRefundAmount(book.getPrice() * 0.5F * request.getBookCount());
    rentalDetailRepository.save(rentalDetail);

    // Cập nhật tổng hóa đơn khi cho thuê sách
    Float totalDeposit = rentalTransaction.getDepositTotal() + (book.getPrice() * rentalDetail.getBookCount());
    Float totalRefundAmount = rentalTransaction.getRefundAmountTotal() + rentalDetail.getRefundAmount();
    rentalTransaction.setDepositTotal(totalDeposit);
    rentalTransaction.setRefundAmountTotal(totalRefundAmount);
    rentalTransaction.setTotalBorrowed(rentalTransaction.getTotalBorrowed() + rentalDetail.getBookCount());
    rentalTransactionRepository.save(rentalTransaction);

    // Giảm số lượng tồn kho của cuốn sách bằng số lượng bookCount trong rental detail
    book.setStock(book.getStock() - rentalDetail.getBookCount());
    bookRepository.save(book);
    apiMessageDto.setMessage("Create rental detail success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RD_L')")
  public ApiMessageDto<ResponseListDto<List<RentalDetailDto>>> getList(RentalDetailCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<RentalDetailDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<RentalDetailDto>> responseListDto = new ResponseListDto<>();
    Page<RentalDetail> rentalDetails = rentalDetailRepository.findAll(criteria.getSpecification(), pageable);
    responseListDto.setContent(rentalDetailMapper.fromEntityToRentalDetailDtoList(rentalDetails.getContent()));
    responseListDto.setTotalElement(rentalDetails.getTotalElements());
    responseListDto.setTotalPage(rentalDetails.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list rental detail success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RD_U')")
  @Transactional
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateRentalDetailForm request, BindingResult bindingResult) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    RentalDetail rentalDetail = rentalDetailRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Rental detail not found", ErrorCode.RENTAL_DETAIL_ERROR_NOT_FOUND));

    Book oldBook = rentalDetail.getBook();
    RentalTransaction oldTransaction = rentalDetail.getRentalTransaction();

    boolean isTransactionChanged = !Objects.equals(oldTransaction.getId(), request.getRentalTransactionId());
    boolean isBookChanged = !Objects.equals(oldBook.getId(), request.getBookId());
    boolean isBookCountChanged = !Objects.equals(rentalDetail.getBookCount(), request.getBookCount());

    if (!isTransactionChanged && !isBookChanged && !isBookCountChanged) {
      apiMessageDto.setMessage("No changes detected");
      return apiMessageDto;
    }

    // Giữ lại giá trị cũ để cập nhật lại transaction đúng cách
    float oldDeposit = oldBook.getPrice() * rentalDetail.getBookCount();
    float oldRefund = oldBook.getPrice() * 0.5F * rentalDetail.getBookCount();

    // Nếu đổi transaction
    if (isTransactionChanged) {
      RentalTransaction newTransaction = rentalTransactionRepository.findById(request.getRentalTransactionId())
          .orElseThrow(() -> new NotFoundException("Rental transaction not found", ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));

      if (!Objects.equals(newTransaction.getState(), MiniBookConstant.RENTAL_STATE_RENTING)) {
        throw new BadRequestException("Cannot update if the state of the rental transaction is not renting",
            ErrorCode.RENTAL_TRANSACTION_ERROR_STATE_NOT_RENTING);
      }

      // Cập nhật transaction cũ (trừ đi giá trị của rentalDetail hiện tại)
      oldTransaction.setDepositTotal(oldTransaction.getDepositTotal() - oldDeposit);
      oldTransaction.setRefundAmountTotal(oldTransaction.getRefundAmountTotal() - oldRefund);
      oldTransaction.setTotalBorrowed(oldTransaction.getTotalBorrowed() - rentalDetail.getBookCount());
      rentalTransactionRepository.save(oldTransaction);

      // Cập nhật transaction mới
      if ((newTransaction.getTotalBorrowed() + request.getBookCount()) > 3) {
        throw new BadRequestException("Cannot borrow more than 3 books",
            ErrorCode.RENTAL_TRANSACTION_ERROR_BORROW_LIMIT);
      }

      float newDeposit = oldBook.getPrice() * request.getBookCount();
      float newRefund = oldBook.getPrice() * 0.5F * request.getBookCount();

      newTransaction.setDepositTotal(newTransaction.getDepositTotal() + newDeposit);
      newTransaction.setRefundAmountTotal(newTransaction.getRefundAmountTotal() + newRefund);
      newTransaction.setTotalBorrowed(newTransaction.getTotalBorrowed() + request.getBookCount());
      rentalTransactionRepository.save(newTransaction);

      rentalDetail.setRentalTransaction(newTransaction);
    }

    // Nếu đổi sách
    if (isBookChanged) {
      Book newBook = bookRepository.findById(request.getBookId())
          .orElseThrow(() -> new NotFoundException("Book not found", ErrorCode.BOOK_ERROR_NOT_FOUND));

      // Trả lại tồn kho sách cũ
      oldBook.setStock(oldBook.getStock() + rentalDetail.getBookCount());
      bookRepository.save(oldBook);

      // Kiểm tra tồn kho sách mới
      if (newBook.getStock() < request.getBookCount()) {
        throw new BadRequestException("Book stock is not enough", ErrorCode.BOOK_ERROR_OUT_OF_STOCK);
      }

      // Giảm tồn kho sách mới
      newBook.setStock(newBook.getStock() - request.getBookCount());
      bookRepository.save(newBook);

      rentalDetail.setBook(newBook);

      // Tính lại giá trị mới theo sách mới
      float newDeposit = newBook.getPrice() * request.getBookCount();
      float newRefund = newBook.getPrice() * 0.5F * request.getBookCount();

      // Cập nhật transaction chứa rental detail này
      oldTransaction.setDepositTotal(oldTransaction.getDepositTotal() - oldDeposit + newDeposit);
      oldTransaction.setRefundAmountTotal(oldTransaction.getRefundAmountTotal() - oldRefund + newRefund);
      oldTransaction.setTotalBorrowed(oldTransaction.getTotalBorrowed() - rentalDetail.getBookCount() + request.getBookCount());
      rentalTransactionRepository.save(oldTransaction);
    }
    // Nếu chỉ đổi số lượng mà không đổi sách
    else if (isBookCountChanged) {
      int delta = request.getBookCount() - rentalDetail.getBookCount();
      if (oldBook.getStock() < delta) {
        throw new BadRequestException("Book stock is not enough", ErrorCode.BOOK_ERROR_OUT_OF_STOCK);
      }

      oldBook.setStock(oldBook.getStock() - delta);
      bookRepository.save(oldBook);

      // Tính lại giá trị mới dựa trên số lượng thay đổi
      float newDeposit = oldBook.getPrice() * request.getBookCount();
      float newRefund = oldBook.getPrice() * 0.5F * request.getBookCount();

      oldTransaction.setDepositTotal(oldTransaction.getDepositTotal() - oldDeposit + newDeposit);
      oldTransaction.setRefundAmountTotal(oldTransaction.getRefundAmountTotal() - oldRefund + newRefund);
      oldTransaction.setTotalBorrowed(oldTransaction.getTotalBorrowed() - rentalDetail.getBookCount() + request.getBookCount());
      rentalTransactionRepository.save(oldTransaction);
    }

    // Cập nhật rental detail
    rentalDetail.setBookCount(request.getBookCount());
    rentalDetail.setRefundAmount(rentalDetail.getBook().getPrice() * 0.5F * request.getBookCount());
    rentalDetailRepository.save(rentalDetail);

    apiMessageDto.setMessage("Update rental detail success");
    return apiMessageDto;
  }



  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RD_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    RentalDetail rentalDetail = rentalDetailRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Rental detail not found", ErrorCode.RENTAL_DETAIL_ERROR_NOT_FOUND));
    Book book = bookRepository.findById(rentalDetail.getBook().getId()).orElseThrow(()
    -> new NotFoundException("Book not found", ErrorCode.BOOK_ERROR_NOT_FOUND));
    RentalTransaction rentalTransaction = rentalTransactionRepository.findById(rentalDetail.getRentalTransaction().getId()).orElseThrow(()
    -> new NotFoundException("Rental transaction not found", ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));

    //Cập nhật stock của book trước khi xóa rental detail
    book.setStock(book.getStock() + rentalDetail.getBookCount());
    bookRepository.save(book);

    //Cập nhật rental transaction trước khi xóa rental detail
    rentalTransaction.setDepositTotal(rentalTransaction.getDepositTotal() - (rentalDetail.getBook().getPrice() * rentalDetail.getBookCount()));
    rentalTransaction.setRefundAmountTotal(rentalTransaction.getRefundAmountTotal() - rentalDetail.getRefundAmount());
    rentalTransaction.setTotalBorrowed(rentalTransaction.getTotalBorrowed() - rentalDetail.getBookCount());
    rentalTransactionRepository.save(rentalTransaction);

    rentalDetailRepository.delete(rentalDetail);
    apiMessageDto.setMessage("Delete rental detail success");
    return apiMessageDto;
  }
}
