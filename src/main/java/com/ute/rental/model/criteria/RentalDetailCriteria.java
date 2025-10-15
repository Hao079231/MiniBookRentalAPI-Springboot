package com.ute.rental.model.criteria;

import com.ute.rental.model.RentalDetail;
import com.ute.rental.model.RentalTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class RentalDetailCriteria {
  private Long rentalTransactionId;

  public Specification<RentalDetail> getSpecification(){
    return new Specification<RentalDetail>() {
      @Override
      public Predicate toPredicate(Root<RentalDetail> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (getRentalTransactionId() != null) {
          Join<RentalDetail, RentalTransaction> transactionJoin = root.join("rentalTransaction");
          predicates.add(cb.equal(transactionJoin.get("id"), getRentalTransactionId()));
        }
        query.orderBy(cb.desc(root.get("createdDate")));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
