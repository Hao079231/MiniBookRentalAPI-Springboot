package com.ute.rental.repository;

import com.ute.rental.model.RentalTransaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalTransactionRepository extends JpaRepository<RentalTransaction, Long>,
    JpaSpecificationExecutor<RentalTransaction> {

  boolean existsByReaderIdAndState(Long id, Integer rentalStateRenting);

  @Modifying
  @Transactional
  @Query("UPDATE RentalTransaction rt SET rt.staff = NULL WHERE rt.staff.id = :staffId")
  void setStaffNullByStaffId(@Param("staffId") Long staffId);
}
