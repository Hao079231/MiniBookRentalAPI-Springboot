package com.ute.rental.repository;

import com.ute.rental.model.RentalDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalDetailRepository extends JpaRepository<RentalDetail, Long>,
    JpaSpecificationExecutor<RentalDetail> {
  List<RentalDetail> findByBookId(Long bookId);

  boolean existsByBookIdAndRentalTransactionId(Long bookId, Long rentalTransactionId);

  void deleteAllByBookId(Long id);
}
