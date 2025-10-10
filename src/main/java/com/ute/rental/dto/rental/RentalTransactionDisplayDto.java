package com.ute.rental.dto.rental;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ute.rental.dto.reader.ReaderDtoForStaff;
import com.ute.rental.dto.staff.ProfileStaffDto;
import java.util.Date;
import lombok.Data;

@Data
public class RentalTransactionDisplayDto {
  private Long id;
  @JsonIgnoreProperties({"phone", "email"})
  private ProfileStaffDto staff;
  @JsonIgnoreProperties({"id", "email", "status"})
  private ReaderDtoForStaff reader;
  private Date dueDate;
}
