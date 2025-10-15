package com.ute.rental.repository;

import com.ute.rental.model.RentalDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalDetailRepository extends JpaRepository<RentalDetail, Long> {
  List<RentalDetail> findByBookId(Long bookId);

  boolean existsByBookIdAndRentalTransactionId(Long bookId, Long rentalTransactionId);

  void deleteAllByBookId(Long id);
}
