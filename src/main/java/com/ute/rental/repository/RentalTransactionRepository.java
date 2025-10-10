package com.ute.rental.repository;

import com.ute.rental.model.RentalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalTransactionRepository extends JpaRepository<RentalTransaction, Long>,
    JpaSpecificationExecutor<RentalTransaction> {

  boolean existsByReaderIdAndState(Long id, Integer rentalStateRenting);
}
