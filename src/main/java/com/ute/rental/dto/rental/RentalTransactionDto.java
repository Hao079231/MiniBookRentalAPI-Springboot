package com.ute.rental.dto.rental;

import com.ute.rental.dto.reader.ReaderDto;
import com.ute.rental.dto.staff.StaffDto;
import java.util.Date;
import lombok.Data;

@Data
public class RentalTransactionDto {
  private Long id;
  private StaffDto staff;
  private ReaderDto reader;
  private Float depositTotal;
  private Float refundAmountTotal;
  private Date dueDate;
  private Integer state;
  private Date createdDate;
  private Date modifiedDate;
}
