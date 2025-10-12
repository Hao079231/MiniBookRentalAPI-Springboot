package com.ute.rental.service;

import com.ute.rental.constant.MiniBookConstant;
import com.ute.rental.model.RentalTransaction;
import com.ute.rental.model.criteria.RentalTransactionCriteria;
import com.ute.rental.repository.RentalTransactionRepository;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RentalTransactionService {
  @Autowired
  RentalTransactionRepository rentalTransactionRepository;

//  @Scheduled(cron = "*/10 * * * * *") // chạy mỗi 10 giây để test nhanh
  @Scheduled(cron = "0 0 0 * * *") //Chạy mỗi ngày lúc 00:00
  @Transactional
  public void autoUpdateOverdueTransactions() {
    Date now = new Date();

    // Tạo criteria để lọc các giao dịch đang RENTING
    RentalTransactionCriteria criteria = new RentalTransactionCriteria();
    criteria.setState(MiniBookConstant.RENTAL_STATE_RENTING);

    List<RentalTransaction> rentingTransactions = rentalTransactionRepository.findAll(criteria.getSpecification());

    int updatedCount = 0;

    for (RentalTransaction rt : rentingTransactions) {
      if (rt.getCreatedDate() != null) {
        long diffInMillis = now.getTime() - rt.getCreatedDate().getTime();
        // Hàm test tự động cập nhật trạng thái thành overdue
//        long diffInSeconds = diffInMillis / 1000;
//        if (diffInSeconds > 30) {
//          rt.setState(MiniBookConstant.RENTAL_STATE_OVERDUE);
//          rentalTransactionRepository.save(rt);
//          updatedCount++;
//        }

        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
        if (diffInDays > 14) {
          rt.setState(MiniBookConstant.RENTAL_STATE_OVERDUE);
          rentalTransactionRepository.save(rt);
          updatedCount++;
        }
      }
    }

    if (updatedCount > 0) {
      log.info("[AUTO UPDATE] Đã cập nhật {} giao dịch sang trạng thái OVERDUE.", updatedCount);
    } else {
      log.info("[AUTO UPDATE] Không có giao dịch nào quá hạn.");
    }
  }
}
