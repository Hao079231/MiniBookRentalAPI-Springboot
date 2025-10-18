package com.ute.rental.repository;

import com.ute.rental.model.RentalDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalDetailRepository extends JpaRepository<RentalDetail, Long>,
    JpaSpecificationExecutor<RentalDetail> {
  List<RentalDetail> findByBookId(Long bookId);

  boolean existsByBookIdAndRentalTransactionId(Long bookId, Long rentalTransactionId);

  void deleteAllByBookId(Long id);

  @Query("SELECT rd FROM RentalDetail rd JOIN FETCH rd.book WHERE rd.rentalTransaction.id = :rentalTransactionId")
  List<RentalDetail> findByRentalTransactionIdWithBook(@Param("rentalTransactionId") Long rentalTransactionId);

  @Modifying
  @Query("DELETE FROM RentalDetail rd WHERE rd.rentalTransaction.id IN :transactionIds")
  void deleteAllByRentalTransactionIdIn(@Param("transactionIds") List<Long> transactionIds);
}
