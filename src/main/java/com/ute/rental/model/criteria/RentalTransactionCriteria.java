package com.ute.rental.model.criteria;

import com.ute.rental.model.Reader;
import com.ute.rental.model.RentalTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class RentalTransactionCriteria implements Serializable {
  private static final long serialVerionUID = 1L;
  private String readerName;
  private String phone;
  private Integer state;

  public Specification<RentalTransaction> getSpecification(){
    return new Specification<RentalTransaction>() {
      @Override
      public Predicate toPredicate(Root<RentalTransaction> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotEmpty(getReaderName())) {
          Join<RentalTransaction, Reader> readerJoin = root.join("reader");
          predicates.add(cb.like(cb.lower(readerJoin.get("name")), "%" + getReaderName().toLowerCase() + "%"));
        }

        if (StringUtils.isNotBlank(getPhone())) {
          Join<RentalTransaction, Reader> readerJoin = root.join("reader");
          predicates.add(cb.like(readerJoin.get("phone"), "%" + getPhone() + "%"));
        }
        if (getState() != null) { // 🔹 thêm điều kiện lọc theo state
          predicates.add(cb.equal(root.get("state"), getState()));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
