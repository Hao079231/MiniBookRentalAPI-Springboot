package com.ute.rental.controller;

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
import com.ute.rental.repository.BookRepository;
import com.ute.rental.repository.RentalDetailRepository;
import com.ute.rental.repository.RentalTransactionRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    Book book = bookRepository.findById(request.getBookId()).orElseThrow(()
    -> new NotFoundException("Book not found", ErrorCode.BOOK_ERROR_NOT_FOUND));
    if (Objects.equals(book.getStock(), 0)){
      throw new BadRequestException("Book stock is out", ErrorCode.BOOK_ERROR_OUT_OF_STOCK);
    }
    RentalTransaction rentalTransaction = rentalTransactionRepository.findById(request.getRentalTransactionId()).orElseThrow(()
    -> new NotFoundException("Rental transaction not found", ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));
    if (rentalTransaction.getTotalBorrowed() > 3){
      throw new BadRequestException("Borrowed 3 books", ErrorCode.RENTAL_TRANSACTION_ERROR_BORROW);
    }
    RentalDetail rentalDetail = new RentalDetail();
    rentalDetail.setBook(book);
    rentalDetail.setRentalTransaction(rentalTransaction);
    rentalDetail.setRefundAmount(book.getPrice() * 0.5F);
    rentalDetailRepository.save(rentalDetail);

    // Cập nhật tổng hóa đơn khi cho thuê sách
    Float totalDeposit = rentalTransaction.getDepositTotal() + book.getPrice();
    Float totalRefundAmount = rentalTransaction.getRefundAmountTotal() + rentalDetail.getRefundAmount();
    rentalTransaction.setDepositTotal(totalDeposit);
    rentalTransaction.setRefundAmountTotal(totalRefundAmount);
    rentalTransaction.setTotalBorrowed(rentalTransaction.getTotalBorrowed() + 1);
    rentalTransactionRepository.save(rentalTransaction);

    // Giảm số lượng tồn kho của cuốn sách đi 1
    book.setStock(book.getStock() - 1);
    bookRepository.save(book);
    apiMessageDto.setMessage("Create rental detail success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RD_L')")
  public ApiMessageDto<ResponseListDto<List<RentalDetailDto>>> getList(){
    ApiMessageDto<ResponseListDto<List<RentalDetailDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<RentalDetailDto>> responseListDto = new ResponseListDto<>();
    Page<RentalDetail> rentalDetails = rentalDetailRepository.findAll(PageRequest.of(0, 10));
    responseListDto.setContent(rentalDetailMapper.fromEntityToRentalDetailDtoList(rentalDetails.getContent()));
    responseListDto.setTotalElement(rentalDetails.getTotalElements());
    responseListDto.setTotalPage(rentalDetails.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list rental detail success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RD_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateRentalDetailForm request, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    RentalDetail rentalDetail = rentalDetailRepository.findById(request.getId()).orElseThrow(()
    -> new NotFoundException("Rental detail not found", ErrorCode.RENTAL_DETAIL_NOT_FOUND));
    RentalTransaction rentalTransaction = rentalTransactionRepository.findById(request.getRentalTransactionId()).orElseThrow(()
        -> new NotFoundException("Rental transaction not found", ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));
    Book book = bookRepository.findById(request.getId()).orElseThrow(()
    -> new NotFoundException("Book not found", ErrorCode.BOOK_ERROR_NOT_FOUND));
    // Cập nhật thông tin của book cũ và book mới khi mà rental detail đổi id book
    if (!Objects.equals(rentalDetail.getBook().getId(), request.getBookId())){
      Book oldBook = bookRepository.findById(rentalDetail.getBook().getId()).orElseThrow(()
          -> new NotFoundException("Book not found", ErrorCode.BOOK_ERROR_NOT_FOUND));
      oldBook.setStock(oldBook.getStock() + 1);
      bookRepository.save(oldBook);

      if (Objects.equals(book.getStock(), 0)){
        throw new BadRequestException("Book stock is out", ErrorCode.BOOK_ERROR_OUT_OF_STOCK);
      }
      book.setStock(book.getStock() - 1);
      bookRepository.save(book);
    }

    boolean flag = false;
    // Cập nhật thông tin của rental transaction cũ trước khi rentail detail cập nhật id rental transaction mới
    if (!Objects.equals(rentalDetail.getRentalTransaction().getId(), request.getRentalTransactionId())){
      flag = true;
      RentalTransaction oldRentalTransaction = rentalTransactionRepository.findById(rentalDetail.getRentalTransaction().getId()).orElseThrow(()
      -> new NotFoundException("Rental transaction not found", ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));
      oldRentalTransaction.setDepositTotal(oldRentalTransaction.getDepositTotal() - rentalDetail.getBook().getPrice());
      oldRentalTransaction.setRefundAmountTotal(oldRentalTransaction.getRefundAmountTotal() - rentalDetail.getRefundAmount());
      oldRentalTransaction.setTotalBorrowed(oldRentalTransaction.getTotalBorrowed() - 1);
      rentalTransactionRepository.save(oldRentalTransaction);

      if (rentalTransaction.getTotalBorrowed() > 3){
        throw new BadRequestException("Borrowed 3 books", ErrorCode.RENTAL_TRANSACTION_ERROR_BORROW);
      }
    }

    rentalDetail.setBook(book);
    rentalDetail.setRentalTransaction(rentalTransaction);
    rentalDetail.setRefundAmount(book.getPrice() * 0.5F);
    rentalDetailRepository.save(rentalDetail);

    if (flag){
      rentalTransaction.setDepositTotal(rentalTransaction.getDepositTotal() + book.getPrice());
      rentalTransaction.setRefundAmountTotal(rentalDetail.getRefundAmount() + rentalDetail.getRefundAmount());
      rentalTransaction.setTotalBorrowed(rentalTransaction.getTotalBorrowed() + 1);
      rentalTransactionRepository.save(rentalTransaction);
    }
    apiMessageDto.setMessage("Update rental detail success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RD_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    RentalDetail rentalDetail = rentalDetailRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Rental detail not found", ErrorCode.RENTAL_DETAIL_NOT_FOUND));
    Book book = bookRepository.findById(rentalDetail.getBook().getId()).orElseThrow(()
    -> new NotFoundException("Book not found", ErrorCode.BOOK_ERROR_NOT_FOUND));
    RentalTransaction rentalTransaction = rentalTransactionRepository.findById(rentalDetail.getRentalTransaction().getId()).orElseThrow(()
    -> new NotFoundException("Rental transaction not found", ErrorCode.RENTAL_TRANSACTION_ERROR_NOT_FOUND));

    //Cập nhật stock của book trước khi xóa rental detail
    book.setStock(book.getStock() + 1);
    bookRepository.save(book);

    //Cập nhật rental transaction trước khi xóa rental detail
    rentalTransaction.setDepositTotal(rentalTransaction.getDepositTotal() - rentalDetail.getBook().getPrice());
    rentalTransaction.setRefundAmountTotal(rentalTransaction.getRefundAmountTotal() - rentalDetail.getRefundAmount());
    rentalTransaction.setTotalBorrowed(rentalTransaction.getTotalBorrowed() - 1);
    rentalTransactionRepository.save(rentalTransaction);

    rentalDetailRepository.delete(rentalDetail);
    apiMessageDto.setMessage("Delete rental detail success");
    return apiMessageDto;
  }
}
