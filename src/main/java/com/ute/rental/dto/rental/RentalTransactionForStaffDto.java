package com.ute.rental.dto.rental;

import com.ute.rental.dto.reader.ReaderDtoForStaff;
import com.ute.rental.dto.staff.ProfileStaffDto;
import java.util.Date;
import lombok.Data;

@Data
public class RentalTransactionForStaffDto {
  private Long id;
  private ReaderDtoForStaff reader;
  private ProfileStaffDto staff;
  private Float depositTotal;
  private Float refundAmountTotal;
  private Date dueDate;
  private Integer state;
  private Date createdDate;
}
