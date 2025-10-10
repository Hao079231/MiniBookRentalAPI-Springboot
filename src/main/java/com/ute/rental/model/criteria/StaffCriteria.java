package com.ute.rental.model.criteria;

import com.ute.rental.model.Account;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class StaffCriteria implements Serializable {
  private static final long serialVerionUID = 1L;
  private String username;
  private String email;
  private String phone;
  private Integer status;
  private Integer kind;

  public Specification<Account> getSpecification(){
    return new Specification<Account>() {
      @Override
      public Predicate toPredicate(Root<Account> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotEmpty(getUsername())) {
          predicates.add(cb.like(cb.lower(root.get("username")), "%" + getUsername().toLowerCase() + "%"));
        }

        if (StringUtils.isNotEmpty(getEmail())) {
          predicates.add(cb.like(cb.lower(root.get("email")), "%" + getEmail().toLowerCase() + "%"));
        }

        if (StringUtils.isNotBlank(getPhone())) {
          predicates.add(cb.like(root.get("phone"), "%" + getPhone() + "%"));
        }


        if (getStatus() != null){
          predicates.add(cb.equal(root.get("status"), getStatus()));
        }

        if (getKind() != null){
          predicates.add(cb.equal(root.get("kind"), getKind()));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
